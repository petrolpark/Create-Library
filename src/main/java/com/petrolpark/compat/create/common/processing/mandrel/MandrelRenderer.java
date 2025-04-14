package com.petrolpark.compat.create.common.processing.mandrel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.PetrolparkPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class MandrelRenderer extends KineticBlockEntityRenderer<MandrelBlockEntity> {

    public MandrelRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(MandrelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        //TODO Flywheel visual
        BlockState state = getRenderedBlockState(be);
		renderRotatingBuffer(be, getRotatedModel(be, state), ms, buffer.getBuffer(getRenderType(be, state)), light);
    };
    
    @Override
    protected SuperByteBuffer getRotatedModel(MandrelBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing(PetrolparkPartialModels.MANDREL_SHAFT, state, state.getValue(MandrelBlock.HORIZONTAL_FACING).getOpposite());
    };
};
