package com.petrolpark;

import com.petrolpark.core.data.loot.modifier.AddEntryLootPoolModifier;
import com.petrolpark.core.data.loot.modifier.AddPoolLootTableModifier;
import com.petrolpark.core.data.loot.modifier.LootPoolEntryModifierType;
import com.petrolpark.core.data.loot.modifier.LootTableModifierType;
import com.petrolpark.core.data.loot.modifier.ModifyNestedLootTableModifier;
import com.petrolpark.core.data.loot.modifier.ModifyPoolEntryLootTableModifier;
import com.petrolpark.core.data.loot.modifier.SetQualityLootPoolEntryModifier;
import com.petrolpark.core.data.loot.modifier.SetWeightLootPoolEntryModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkLootModifierTypes {
    
    public static final RegistryEntry<LootTableModifierType, LootTableModifierType>

    ADD_POOL = Petrolpark.REGISTRATE.lootTableModifierType("add_pool", AddPoolLootTableModifier.CODEC),
    ADD_ENTRY = Petrolpark.REGISTRATE.lootTableModifierType("add_entry", AddEntryLootPoolModifier.CODEC),
    MODIFY_ENTRY = Petrolpark.REGISTRATE.lootTableModifierType("modify_entry", ModifyPoolEntryLootTableModifier.CODEC);

    public static final RegistryEntry<LootPoolEntryModifierType, LootPoolEntryModifierType>

    MODIFY_NESTED_TABLE = Petrolpark.REGISTRATE.lootPoolEntryModifierType("modify_nested_table", ModifyNestedLootTableModifier.CODEC),
    SET_WEIGHT = Petrolpark.REGISTRATE.lootPoolEntryModifierType("set_weight", SetWeightLootPoolEntryModifier.CODEC),
    SET_QUALITY = Petrolpark.REGISTRATE.lootPoolEntryModifierType("set_quality", SetQualityLootPoolEntryModifier.CODEC);

    public static final void register() {};
};
