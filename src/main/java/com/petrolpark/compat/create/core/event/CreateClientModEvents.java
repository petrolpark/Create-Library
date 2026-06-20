package com.petrolpark.compat.create.core.event;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.world.block.tube.ClientTubePlacementHandler;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class CreateClientModEvents {
    
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Petrolpark.asResource("tube_info"), ClientTubePlacementHandler.OVERLAY);
    };
};
