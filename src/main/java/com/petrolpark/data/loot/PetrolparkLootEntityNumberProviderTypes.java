package com.petrolpark.data.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.data.loot.numberprovider.entity.EquipmentNumberProvider;
import com.petrolpark.data.loot.numberprovider.entity.ExperienceLevelNumberProvider;
import com.petrolpark.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkLootEntityNumberProviderTypes {
    
    public static final RegistryEntry<LootEntityNumberProviderType>
    
    EQUIPMENT = REGISTRATE.get().lootEntityNumberProviderType("equipment_property", new EquipmentNumberProvider.Serializer()),
    EXPERIENCE_LEVEL = REGISTRATE.get().lootEntityNumberProviderType("experience_level", ExperienceLevelNumberProvider::new);

    public static final void register() {};
};
