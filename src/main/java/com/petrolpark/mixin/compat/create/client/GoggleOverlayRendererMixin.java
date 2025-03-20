package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.compat.create.core.tube.ClientTubePlacementHandler;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(GoggleOverlayRenderer.class)
public class GoggleOverlayRendererMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/equipment/goggles/GoggleOverlayRenderer;renderOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    private static void inRenderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (ClientTubePlacementHandler.active()) ci.cancel();
    };
};
