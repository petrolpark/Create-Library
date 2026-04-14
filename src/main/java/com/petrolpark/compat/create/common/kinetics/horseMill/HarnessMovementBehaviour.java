package com.petrolpark.compat.create.common.kinetics.horseMill;

import java.util.Map;
import java.util.UUID;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class HarnessMovementBehaviour implements MovementBehaviour {
    
    @Override
    public void startMoving(MovementContext context) {
        MovementBehaviour.super.startMoving(context);
        final int indexOf = context.contraption instanceof HorseMillContraption horseMillContraption ? horseMillContraption.getHarnesses().indexOf(context.localPos) : -1;
        context.data.putInt("HarnessIndex", indexOf);
    };

    @Override
    public void tick(MovementContext context) {
        if (!context.stall || !context.data.contains("StalledPos")) return;
        BlockPos pos = NBTHelper.readBlockPos(context.data, "StalledPos");
        if (Block.canSupportRigidBlock(context.world, pos.below())) cancelStall(context);
    };

    @Override
    public void cancelStall(MovementContext context) {
        MovementBehaviour.super.cancelStall(context);
        context.data.remove("StalledPos");
    };

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        MovementBehaviour.super.visitNewPosition(context, pos);

		if (!(context.contraption.entity instanceof HorseMillContraptionEntity contraptionEntity)) return;
		final int index = context.data.getInt("HarnessIndex");
		if (index == -1) return;

        Entity passenger = null;
        if (contraptionEntity.getContraption().getHarnessMapping().containsValue(index)) {
            entries: for (Map.Entry<UUID, Integer> entry : contraptionEntity.getContraption().getHarnessMapping().entrySet()) {
                if (entry.getValue() != index) continue; 
                for (Entity entity : contraptionEntity.getPassengers()) {
                    if (entity.getUUID().equals(entry.getKey())) {
                        passenger = entity;
                        break entries;
                    };
                };
            };
        };

        if (passenger != null) {
            HorseMillProperties.get(passenger).flatMap(HorseMillProperties::harnessModelLocation).ifPresent(rl -> context.data.putString("HarnessModel", rl.toString()));
            if (!Block.canSupportRigidBlock(context.world, pos.below())) {
                context.data.put("StalledPos", NbtUtils.writeBlockPos(pos));
                context.stall = true;
            };
        } else {
            context.data.remove("HarnessModel");
        };
    };

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!context.data.contains("HarnessModel")) return;
        CachedBuffers.partial(PartialModel.of(ResourceLocation.parse(context.data.getString("HarnessModel"))), Blocks.AIR.defaultBlockState())
            .transform(matrices.getModel())
            .center()
            .rotateToFace(context.state.getValue(HarnessBlock.FACING))
            .uncenter()
            .light(LevelRenderer.getLightColor(renderWorld, context.localPos))
			.useLevelLight(context.world, matrices.getWorld())
            .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.CUTOUT_MIPPED));
    };
};
