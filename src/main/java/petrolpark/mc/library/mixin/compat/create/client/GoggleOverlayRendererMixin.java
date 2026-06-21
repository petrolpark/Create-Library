package petrolpark.mc.library.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import petrolpark.mc.library.compat.create.core.world.block.tube.ClientTubePlacementHandler;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(GoggleOverlayRenderer.class)
public class GoggleOverlayRendererMixin {
    
    @WrapMethod(
        method = "Lcom/simibubi/create/content/equipment/goggles/GoggleOverlayRenderer;renderOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
        remap = false
    )
    private static void inRenderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (!ClientTubePlacementHandler.active()) original.call(graphics, deltaTracker);
    };
};
