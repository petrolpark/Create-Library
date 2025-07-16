package com.petrolpark.compat.curios;

import com.petrolpark.PetrolparkItems;
import com.petrolpark.common.item.shulkerbelt.ShulkerBeltLayer;
import com.petrolpark.compat.curios.renderer.CuriosRenderers;

import net.neoforged.bus.api.IEventBus;

public class CuriosClient {
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {

        ShulkerBeltLayer.WEARING_PREDICATES.add(Curios.wearingCurioPredicate(PetrolparkItems.SHULKER_BELT::isIn, "belt"));

        modEventBus.addListener(CuriosRenderers::onLayerRegister);
    };
};
