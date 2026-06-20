package com.petrolpark.compat.create.shared.content.processing.blender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.shared.registry.SharedPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.processing.basin.BasinBlock;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class BlenderRenderer extends KineticBlockEntityRenderer<BlenderBlockEntity> {
    
    public BlenderRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	};

	@Override
	@SuppressWarnings("null")
	protected void renderSafe(BlenderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
		if (VisualizationManager.supportsVisualization(be.getLevel())) return;

		BlockState aboveState = be.getLevel().getBlockState(be.getBlockPos().above());
		if (aboveState.isAir() || aboveState.getBlock() instanceof BasinBlock) {
			BlockState state = getRenderedBlockState(be);

			renderRotatingBuffer(be, CachedBuffers.partial(SharedPartialModels.BLENDER_BLADES, state)
				.translateY(aboveState.isAir() ? 0f : 2 / 16f), ms, buffer.getBuffer(RenderType.CUTOUT), light);
		};
	};

	@Override
	protected SuperByteBuffer getRotatedModel(BlenderBlockEntity be, BlockState state) {
		return CachedBuffers.partial(SharedPartialModels.BLENDER_COG, state);
	};
};
