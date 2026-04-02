package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.create.common.kinetics.horseMill.HorseMillContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkCreateContraptionTypes {
    
    public static final RegistryEntry<ContraptionType, ContraptionType> HORSE_MILL = REGISTRATE.simple("horse_mill", CreateRegistries.CONTRAPTION_TYPE, () -> new ContraptionType(HorseMillContraption::new));

    public static final void register() {};
};
