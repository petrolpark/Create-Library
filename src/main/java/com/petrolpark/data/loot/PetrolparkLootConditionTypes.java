package com.petrolpark.data.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.data.loot.predicate.ParameterSuppliedLootCondition;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class PetrolparkLootConditionTypes {
    
    public static final RegistryEntry<LootItemConditionType> PARAMETERS_SUPPLIED = REGISTRATE.lootConditionType("parameters_supplied", new ParameterSuppliedLootCondition.Serializer());

    public static final void register() {};
};
