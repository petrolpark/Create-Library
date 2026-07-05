package petrolpark.mc.library.compat.create.core.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.block.tube.ClientTubePlacementHandler;
import petrolpark.mc.library.compat.create.core.world.dough.client.DoughItemRenderer;
import petrolpark.mc.library.compat.create.core.world.dough.client.RolledDoughModel;
import petrolpark.mc.library.compat.create.core.world.dough.rollingPin.RollingPinItemRenderer;

public class CreateClientModEvents {
    
    @SubscribeEvent
    public static final void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        RolledDoughModel.onRegisterGeometryLoaders(event);
    };

    @SubscribeEvent
    public static final void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        DoughItemRenderer.onRegisterClientExtensions(event);
        RollingPinItemRenderer.onRegisterClientExtensions(event);
    };

    @SubscribeEvent
    public static final void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Petrolpark.asResource("tube_info"), ClientTubePlacementHandler.OVERLAY);
    };
};
