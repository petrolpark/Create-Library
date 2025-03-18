package com.petrolpark.compat.curios;

import com.petrolpark.compat.curios.renderer.CuriosRenderers;

import net.neoforged.bus.api.IEventBus;

public class CuriosClient {
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {
        modEventBus.addListener(CuriosRenderers::onLayerRegister);
    };
};
