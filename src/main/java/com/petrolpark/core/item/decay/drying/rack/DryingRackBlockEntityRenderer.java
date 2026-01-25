package com.petrolpark.core.item.decay.drying.rack;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.PetrolparkItemDisplayContexts;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.ItemStack;

public class DryingRackBlockEntityRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {

    protected final ItemRenderer itemRenderer;

    public DryingRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    };

    @Override
    public void render(@Nonnull DryingRackBlockEntity rack, float partialTick, @Nonnull PoseStack ms, @Nonnull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        final ItemStack stack = rack.inv.getStackInSlot(0);
        if (stack.isEmpty()) return;
        ms.pushPose(); {
            ms.translate(8 / 16f, 8 / 16f, 8 / 16f);
            ms.scale(0.5f, 0.5f, 0.5f);
            TransformStack.of(ms).rotateYDegrees(90f);
            if (rack.getBlockState().getValue(DryingRackBlock.AXIS) == Axis.X) TransformStack.of(ms).rotateYDegrees(90f);
            itemRenderer.render(stack, PetrolparkItemDisplayContexts.DRYING_RACK, false, ms, bufferSource, packedLight, packedOverlay, itemRenderer.getModel(stack, rack.getLevel(), null, 0));
        }; ms.popPose();
    };
    
};
