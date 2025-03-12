package com.petrolpark.data.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.data.loot.numberprovider.entity.EquipmentNumberProvider;
import com.petrolpark.data.loot.numberprovider.entity.ExperienceLevelNumberProvider;
import com.petrolpark.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkLootEntityNumberProviderTypes {
    
    public static final RegistryEntry<LootEntityNumberProviderType, LootEntityNumberProviderType>
    
    EQUIPMENT = REGISTRATE.lootEntityNumberProviderType("equipment_property", EquipmentNumberProvider.CODEC),
    EXPERIENCE_LEVEL = REGISTRATE.lootEntityNumberProviderType("experience_level", MapCodec.unit(ExperienceLevelNumberProvider::new));

    public static final void register() {};
};
