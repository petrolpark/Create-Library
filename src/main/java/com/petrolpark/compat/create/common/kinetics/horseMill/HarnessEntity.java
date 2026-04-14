package com.petrolpark.compat.create.common.kinetics.horseMill;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.PetrolparkCreateEntityTypes;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HarnessEntity extends SeatEntity {

    public HarnessEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    };

    public HarnessEntity(Level level) {
        this(PetrolparkCreateEntityTypes.HARNESS.get(), level);
        noPhysics = true;
    };

    @Override
    public void tick() {
        final BlockState state = level().getBlockState(blockPosition());
        if (state.getBlock() instanceof HarnessBlock) {
            final float angle = -AngleHelper.horizontalAngle(state.getValue(HarnessBlock.FACING));
            for (Entity entity : getPassengers()) setFacing(entity, angle);
            if (isVehicle()) return;
        };
		discard();
    };

    public static final void setFacing(Entity entity, float angle) {
        if (!(entity instanceof LivingEntity living)) return;
        if (entity.level().isClientSide()) {
            living.lerpTo(0, 0, 0, 0, 0, 0);
            living.lerpHeadTo(0, 0);
            living.setYRot(angle);
            living.setXRot(0);
            living.yBodyRot = angle;
            living.yHeadRot = angle;
        } else {
            living.setYRot(angle);
        };
    };

    @Override
	protected void positionRider(Entity entity, Entity.MoveFunction callback) {
		if (!hasPassenger(entity)) return;
        Vec3 pos = Vec3.atBottomCenterOf(blockPosition());
        final BlockState state = level().getBlockState(blockPosition());
        if (state.getBlock() instanceof HarnessBlock) {
            final Optional<Vec3> offset = HorseMillProperties.get(entity).map(HorseMillProperties::positionOffset);
            if (offset.isPresent()) pos = pos.add(VecHelper.rotate(offset.get(), AngleHelper.horizontalAngle(state.getValue(HarnessBlock.FACING)), Axis.Y));
        };
		callback.accept(entity, pos.x(), pos.y(), pos.z());
	};

    public static class Renderer extends SeatEntity.Render {

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
        };

        @Override
        public boolean shouldRender(SeatEntity seatEntity, Frustum frustum, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
            return !seatEntity.getPassengers().isEmpty();
        };

        @Override
        public void render(@Nonnull SeatEntity entity, float entityYaw, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight) {
            final BlockState state = entity.level().getBlockState(entity.blockPosition());
            if (!(state.getBlock() instanceof HarnessBlock)) return;
            Optional.ofNullable(entity.getFirstPassenger())
                .flatMap(HorseMillProperties::get)
                .flatMap(HorseMillProperties::harnessModelLocation)
                .map(PartialModel::of)
                .ifPresent(model -> CachedBuffers.partial(model, Blocks.AIR.defaultBlockState())
                    .rotateToFace(state.getValue(HarnessBlock.FACING))
                    .uncenter()
                    .translate(0f, 0.5f, 0f)
                    .light(packedLight)
                    .renderInto(poseStack, bufferSource.getBuffer(RenderType.CUTOUT_MIPPED))
                );
        };

    };
    
};
