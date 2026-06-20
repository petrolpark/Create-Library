package com.petrolpark.compat.create.shared.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HorseMillContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class SharedContraptionTypes {
    
    public static final RegistryEntry<ContraptionType, ContraptionType> HORSE_MILL = REGISTRATE.simple("horse_mill", CreateRegistries.CONTRAPTION_TYPE, () -> new ContraptionType(HorseMillContraption::new));

    public static final void register() {};
};
