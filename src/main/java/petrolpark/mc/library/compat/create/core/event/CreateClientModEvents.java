package petrolpark.mc.library.compat.create.core.event;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.block.tube.ClientTubePlacementHandler;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class CreateClientModEvents {
    
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Petrolpark.asResource("tube_info"), ClientTubePlacementHandler.OVERLAY);
    };
};
