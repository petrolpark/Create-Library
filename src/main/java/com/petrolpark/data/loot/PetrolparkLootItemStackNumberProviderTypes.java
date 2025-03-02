package com.petrolpark.data.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.data.loot.numberprovider.itemstack.CountItemStackNumberProvider;
import com.petrolpark.data.loot.numberprovider.itemstack.EnchantmentLevelNumberProvider;
import com.petrolpark.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkLootItemStackNumberProviderTypes {
    
    public static final RegistryEntry<LootItemStackNumberProviderType>
    
    COUNT = REGISTRATE.get().lootItemStackNumberProviderType("count", CountItemStackNumberProvider::new),
    ENCHANTMENT_LEVEL = REGISTRATE.get().lootItemStackNumberProviderType("enchantment_level", new EnchantmentLevelNumberProvider.Serializer());

    public static final void register() {};

};
