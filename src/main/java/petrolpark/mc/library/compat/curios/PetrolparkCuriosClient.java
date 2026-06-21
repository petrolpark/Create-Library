package petrolpark.mc.library.compat.curios;

import petrolpark.mc.library.compat.curios.renderer.CuriosRenderers;
import petrolpark.mc.library.shared.registry.SharedItems;
import petrolpark.mc.library.shared.world.item.shulkerbelt.ShulkerBeltLayer;

import net.neoforged.bus.api.IEventBus;

public class PetrolparkCuriosClient {
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {

        ShulkerBeltLayer.WEARING_PREDICATES.add(PetrolparkCurios.wearingCurioPredicate(SharedItems.SHULKER_BELT::isIn, "belt"));

        modEventBus.addListener(CuriosRenderers::onLayerRegister);
    };
};
