package com.petrolpark.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.data.loot.condition.ParameterSuppliedLootCondition;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class PetrolparkLootConditionTypes {
    
    public static final RegistryEntry<LootItemConditionType, LootItemConditionType> PARAMETERS_SUPPLIED = REGISTRATE.lootConditionType("parameters_supplied", ParameterSuppliedLootCondition.CODEC);

    public static final void register() {};
};
