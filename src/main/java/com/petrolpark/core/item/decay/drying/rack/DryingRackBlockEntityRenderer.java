package com.petrolpark.core.item.decay.drying.rack;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DryingRackBlockEntityRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {

    public DryingRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        
    };

    @Override
    public void render(@Nonnull DryingRackBlockEntity rack, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // TODO Auto-generated method stub
    };
    
};
