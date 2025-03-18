package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.recipe.condition.ConfigBooleanCondition;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.conditions.ICondition;

public class PetrolparkDataLoadingConditions {
    
    public static final RegistryEntry<MapCodec<? extends ICondition>, MapCodec<ConfigBooleanCondition>> CONFIG_BOOLEAN = REGISTRATE.dataLoadingCondition("config_bool", ConfigBooleanCondition.CODEC);

    public static final void register() {};
};
