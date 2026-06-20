package com.petrolpark.compat.curios;

import com.petrolpark.compat.curios.renderer.CuriosRenderers;
import com.petrolpark.shared.registry.SharedItems;
import com.petrolpark.shared.world.item.shulkerbelt.ShulkerBeltLayer;

import net.neoforged.bus.api.IEventBus;

public class PetrolparkCuriosClient {
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {

        ShulkerBeltLayer.WEARING_PREDICATES.add(PetrolparkCurios.wearingCurioPredicate(SharedItems.SHULKER_BELT::isIn, "belt"));

        modEventBus.addListener(CuriosRenderers::onLayerRegister);
    };
};
