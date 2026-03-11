package com.petrolpark.compat.create.core.dough;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DoughBlockEntityRenderer implements BlockEntityRenderer<DoughBlockEntity> {

    public DoughBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    };

    @Override
    public void render(@Nonnull DoughBlockEntity be, float partialTicks, @Nonnull PoseStack ms, @Nonnull MultiBufferSource bufferSource, int light, int overlay) {
        be.renderingData.render(be.getBlockState(), partialTicks, ms, bufferSource.getBuffer(RenderType.SOLID), light);
    };
    
};
