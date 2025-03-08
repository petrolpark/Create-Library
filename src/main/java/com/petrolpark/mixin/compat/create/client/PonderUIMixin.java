package com.petrolpark.mixin.compat.create.client;

import net.createmod.catnip.gui.element.AbstractRenderElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.client.ponder.instruction.HighlightTagInstruction;
import com.petrolpark.mixin.compat.create.accessor.client.SimpleRenderElementAccessor;
import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.ui.PonderButton;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.createmod.catnip.animation.LerpedFloat;

import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

@Mixin(PonderUI.class)
public class PonderUIMixin {
    
    @Inject(
        //method = "lambda$renderPonderTags$9(Lcom/mojang/blaze3d/vertex/PoseStack;IIZFFLnet/minecraft/client/gui/GuiGraphics;DI)V",
        method = "lambda$renderWidgets$18",
        at = @At(
            value = "INVOKE",
            target = "Lnet/createmod/catnip/animation/LerpedFloat;tickChaser()V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inRenderPonderTags(PoseStack ms, int mouseX, int mouseY, boolean highlightAll, List _list, float fade, float partialTicks, GuiGraphics graphics, double guiScale, int height, CallbackInfo ci, PonderTag _tag, LerpedFloat chase, PonderButton button) {
        if (button.getRenderElement() instanceof AbstractRenderElement.SimpleRenderElement element) {
            if (((SimpleRenderElementAccessor)element).getRenderable() instanceof PonderTag tag) {
                if (HighlightTagInstruction.highlightedTags.contains(tag)) chase.updateChaseTarget(1);    
            };
        };
    };
};
