package com.petrolpark.compat.create.event;

import com.petrolpark.compat.create.PetrolparkCreateRegistries;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlockEntity;
import com.petrolpark.compat.create.core.dough.DoughCut;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public class CreateModEvents {

    @SubscribeEvent
    public static final void onDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PetrolparkCreateRegistries.Keys.DOUGH_CUT, DoughCut.DIRECT_CODEC, DoughCut.DIRECT_CODEC);
    };
    
    @SubscribeEvent
    public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
        MandrelBlockEntity.registerCapabilities(event);
    };
};
