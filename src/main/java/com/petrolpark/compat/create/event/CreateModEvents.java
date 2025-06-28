package com.petrolpark.compat.create.event;

import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlockEntity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CreateModEvents {
    
    @SubscribeEvent
    public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
        MandrelBlockEntity.registerCapabilities(event);
    };
};
