package com.petrolpark.itemdecay;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.client.rendering.item.TransparentItemRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DecayingItemRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack trueStack = IDecayingItem.checkDecay(stack);
        ItemRenderer itemRenderer = mc.getItemRenderer();
        if (stack == trueStack) {
            itemRenderer.render(trueStack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel());
        } else {
            float alphaScale = Mth.sin(AnimationTickHolder.getRenderTime() / 10);
            alphaScale *= alphaScale;
            int alpha = (int)(255f * alphaScale);

            BakedModel trueStackModel = itemRenderer.getModel(trueStack, mc.level, null, overlay);
            TransparentItemRenderer.transformAndRenderModel(trueStackModel, ItemDisplayContext.NONE, 0x00FFFFFF | alpha << 24, light, overlay, ms, buffer);
            TransparentItemRenderer.transformAndRenderModel(model.getOriginalModel(), ItemDisplayContext.NONE, 0x00FFFFFF | (255 - alpha) << 24, light, overlay, ms, buffer);
        };
    };
    
};
