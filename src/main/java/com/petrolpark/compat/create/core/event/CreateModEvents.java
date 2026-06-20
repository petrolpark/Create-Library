package com.petrolpark.compat.create.core.event;

import com.petrolpark.compat.create.core.world.dough.DoughCut;
import com.petrolpark.compat.create.core.world.dough.topping.IDoughTopping;
import com.petrolpark.compat.create.registry.PetrolparkCreateRegistries;
import com.petrolpark.compat.create.shared.content.processing.mandrel.MandrelBlockEntity;
import com.petrolpark.compat.create.shared.registry.SharedCreateDataMapTypes;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class CreateModEvents {

    @SubscribeEvent
    public static final void onDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PetrolparkCreateRegistries.Keys.DOUGH_CUT, DoughCut.DIRECT_CODEC, DoughCut.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkCreateRegistries.Keys.DOUGH_TOPPING, IDoughTopping.DIRECT_CODEC, IDoughTopping.DIRECT_CODEC);
    };
    
    @SubscribeEvent
    public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
        MandrelBlockEntity.registerCapabilities(event);
    };

    @SubscribeEvent
    public static final void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        SharedCreateDataMapTypes.onRegisterDataMapTypes(event);
    };
};
