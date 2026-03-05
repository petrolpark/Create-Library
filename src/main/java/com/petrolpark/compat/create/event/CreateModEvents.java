package com.petrolpark.compat.create.event;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateRegistries;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeBlockEntity;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlockEntity;
import com.petrolpark.compat.create.common.processing.meshbasin.MeshBasinBlockEntity;
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

    @SubscribeEvent
    public static final void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        if (SharedFeatureFlag.CENTRIFUGE.enabled()) CentrifugeBlockEntity.onRegisterCapabilities(event);
        if (SharedFeatureFlag.MESH_BASIN.enabled()) MeshBasinBlockEntity.onRegisterCapabilities(event);
    };
};
