package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.common.kinetics.horseMill.HorseMillProperties;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class PetrolparkCreateDataMapTypes {
    
    public static final DataMapType<EntityType<?>, HorseMillProperties> HORSE_MILL_PROPERTIES = DataMapType
        .builder(
            Petrolpark.asResource("horse_mill_properties"),
            Registries.ENTITY_TYPE,
            HorseMillProperties.CODEC
        ).synced(HorseMillProperties.CODEC, true)
        .build();

    public static final void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(HORSE_MILL_PROPERTIES);
    };
};
