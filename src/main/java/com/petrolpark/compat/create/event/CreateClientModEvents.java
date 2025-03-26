package com.petrolpark.compat.create.event;

import com.petrolpark.compat.create.core.tube.ClientTubePlacementHandler;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreateClientModEvents {
    
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "tube_info", ClientTubePlacementHandler.OVERLAY);
    };
};
