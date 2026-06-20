package com.petrolpark.compat.create.shared.content.processing.extrusion;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.compat.create.core.world.block.entity.behaviour.AdvancementBehaviour;
import com.petrolpark.compat.create.shared.registry.SharedCreateCriterionTriggers;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.SuperBufferFactory;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ExtrusionMovementBehaviour implements MovementBehaviour {

    public static final String
    EXTRUDING_TAG_KEY = "Extruding",
    EXTRUDED_TAG_KEY = "Extruded",
    EXTRUSION_DIRECTION_TAG_KEY = "ExtrusionDirection",
    EXTRUSION_DIE_POS_TAG_KEY = "ExtrusionDiePosTagKey",
    EXTRUDED_BLOCK_STATE_TAG_KEY = "ExtrudedBlockState";

    private final RecipeHolder<ExtrusionRecipe> extrusionRecipeHolder;

    public ExtrusionMovementBehaviour(RecipeHolder<ExtrusionRecipe> extrusionRecipeHolder) {
        this.extrusionRecipeHolder = extrusionRecipeHolder;
    };

    @Override
    public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
        CompoundTag data = context.data;
        if (data.getBoolean(EXTRUDING_TAG_KEY)) {
            Direction direction = getDirection(context);
            if (!motion.equals(Vec3.ZERO) && !VecHelper.isVecPointingTowards(motion, direction) && !VecHelper.isVecPointingTowards(motion, direction.getOpposite()))
                abandonExtrusion(context);
        };
    };

    @Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
        BlockState dieState = context.world.getBlockState(pos);
        CompoundTag data = context.data;

        if (dieState.getBlock() instanceof ExtrusionDieBlock && !data.getBoolean(EXTRUDING_TAG_KEY) && !data.getBoolean(EXTRUDED_TAG_KEY)) {
            Axis axis = dieState.getValue(BlockStateProperties.AXIS);

            for (AxisDirection axisDirection : AxisDirection.values()) {
                Direction direction = Direction.get(axisDirection, axis);
                BlockState state = extrusionRecipeHolder.value().extrude(context.state, direction);
                if (VecHelper.isVecPointingTowards(context.relativeMotion, direction) && !state.isAir()) {
                    data.putBoolean(EXTRUDING_TAG_KEY, true);
                    data.putInt(EXTRUSION_DIRECTION_TAG_KEY, direction.ordinal());
                    data.put(EXTRUSION_DIE_POS_TAG_KEY, NbtUtils.writeBlockPos(pos));
                    data.put(EXTRUDED_BLOCK_STATE_TAG_KEY, NbtUtils.writeBlockState(state));
                    break;
                };
            };

        } else if (data.getBoolean(EXTRUDING_TAG_KEY)) {

            Optional<BlockPos> diePosOp = NbtUtils.readBlockPos(data, EXTRUSION_DIE_POS_TAG_KEY);
            if (diePosOp.isEmpty()) return;
            BlockPos diePos = diePosOp.get();
            Direction direction = getDirection(context);

            if (context.contraption.entity instanceof OrientedContraptionEntity oce && oce.getInitialYaw() != oce.yaw) direction = direction.getOpposite();
            
            if (pos.equals(diePos.relative(direction))) {
                BlockState extrudedBlockState = getExtrudedBlockState(context);
                context.contraption.getBlocks().put(context.localPos, new StructureBlockInfo(context.localPos, extrudedBlockState, null)); // Replace the Block in the Contraption now the Extrusion is complete
                if (!context.world.isClientSide()) {
                    AdvancementBehaviour advancementBehaviour = BlockEntityBehaviour.get(context.world, diePos, AdvancementBehaviour.TYPE);
                    if (advancementBehaviour != null) advancementBehaviour.award(SharedCreateCriterionTriggers.EXTRUSION.get().trigger(extrusionRecipeHolder, context.state, extrudedBlockState));
                };
                data.putBoolean(EXTRUDED_TAG_KEY, true);
            };
            
            abandonExtrusion(context);
        };
	};

    @Override
    public void tick(MovementContext context) {
        if (!context.world.isClientSide() || !context.data.getBoolean(EXTRUDING_TAG_KEY)) return;
        Direction direction = getDirection(context);
        if (!VecHelper.isVecPointingTowards(context.motion, direction)) return;
        Optional<BlockPos> diePosOp = NbtUtils.readBlockPos(context.data, EXTRUSION_DIE_POS_TAG_KEY);
        if (diePosOp.isEmpty()) return;
        BlockPos diePos = diePosOp.get();
        Vec3 step = new Vec3(direction.step());
        double speed = context.motion.dot(step);
        BlockState extrudedState = getExtrudedBlockState(context);
        for (Direction face : Iterate.directions) {
            if (face.getAxis() == direction.getAxis()) continue;
            Vec3 loc = VecHelper.getCenterOf(diePos).add(new Vec3(face.step()).scale(0.5f)).add(
                face.getAxis() == Axis.X ? 0f : -0.5f + context.world.getRandom().nextFloat(),
                face.getAxis() == Axis.Y ? 0f : -0.5f + context.world.getRandom().nextFloat(),
                face.getAxis() == Axis.Z ? 0f : -0.5f + context.world.getRandom().nextFloat()
            );
            Vec3 velocity = step.scale(speed).add(new Vec3(face.step()).scale(0.1f));
            context.world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, extrudedState), loc.x(), loc.y(), loc.z(), velocity.x(), velocity.y(), velocity.z());
        };
    };

    @OnlyIn(Dist.CLIENT)
	public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        CompoundTag data = context.data;

        if (!data.getBoolean(EXTRUDING_TAG_KEY) && !data.getBoolean(EXTRUDED_TAG_KEY)) return;
        PoseStack ms = matrices.getViewProjection();
        PoseStack modelTransform = matrices.getModel();
        VertexConsumer vbSolid = buffer.getBuffer(RenderType.solid());

        Direction direction = getDirection(context);
        float progress = 0f;

        if (data.getBoolean(EXTRUDED_TAG_KEY)) {
            progress = 0f;
        } else {
            Optional<BlockPos> diePosOp = NbtUtils.readBlockPos(data, EXTRUSION_DIE_POS_TAG_KEY);
            if (diePosOp.isEmpty()) return;
            BlockPos diePos = diePosOp.get();
            Vec3 displacement = context.position.subtract(Vec3.atLowerCornerOf(diePos)); // Vector between center of Die and of Block being extruded
            progress = (float)direction.getAxis().choose(displacement.x(), displacement.y(), displacement.z());
            boolean invertProgess = direction.getAxisDirection() == AxisDirection.POSITIVE;
            if (context.contraption.entity instanceof OrientedContraptionEntity oce) {
                if (oce.yaw != oce.getInitialYaw()) invertProgess = !invertProgess;
            };
            if (invertProgess) progress = 1f - progress;
        };

        // Making a new model every frame seems like a bad idea but it changes shape continually and I'm not smart enough to do it another way
        ms.pushPose();
        BlockState extrudedState = getExtrudedBlockState(context);
        BakedModel model = new ExtrudedBlockModel(extrudedState, direction, progress);
		SuperByteBuffer extrudedBlockBuffer = SuperBufferFactory.getInstance().createForBlock(model, Blocks.AIR.defaultBlockState());
        if (modelTransform != null) extrudedBlockBuffer.transform(modelTransform);
        
        extrudedBlockBuffer
            .useLevelLight(context.world, matrices.getWorld())
            .renderInto(ms, vbSolid);
        ms.popPose();
    };

    private void abandonExtrusion(MovementContext context) {
        CompoundTag data = context.data;
        data.putBoolean(EXTRUDING_TAG_KEY, false);
        data.remove(EXTRUSION_DIE_POS_TAG_KEY);
        data.remove(EXTRUSION_DIRECTION_TAG_KEY);
        // Don't remove the Block State tag as we still need to know what to render
    };

    private static Direction getDirection(MovementContext context) {
        return Direction.values()[context.data.getInt(EXTRUSION_DIRECTION_TAG_KEY)];
    };

    private static BlockState getExtrudedBlockState(MovementContext context) {
        if (!context.data.contains(EXTRUDED_BLOCK_STATE_TAG_KEY)) return Blocks.AIR.defaultBlockState();
        return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), context.data.getCompound(EXTRUDED_BLOCK_STATE_TAG_KEY));
    };
};
