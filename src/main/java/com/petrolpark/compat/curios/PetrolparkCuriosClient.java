package com.petrolpark.compat.curios;

import com.petrolpark.PetrolparkItems;
import com.petrolpark.common.item.shulkerbelt.ShulkerBeltLayer;
import com.petrolpark.compat.curios.renderer.CuriosRenderers;

import net.neoforged.bus.api.IEventBus;

public class PetrolparkCuriosClient {
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {

        ShulkerBeltLayer.WEARING_PREDICATES.add(PetrolparkCurios.wearingCurioPredicate(PetrolparkItems.SHULKER_BELT::isIn, "belt"));

        modEventBus.addListener(CuriosRenderers::onLayerRegister);
    };
};
