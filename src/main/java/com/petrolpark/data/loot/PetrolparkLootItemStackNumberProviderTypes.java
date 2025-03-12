package com.petrolpark.data.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.data.loot.numberprovider.itemstack.CountItemStackNumberProvider;
import com.petrolpark.data.loot.numberprovider.itemstack.EnchantmentLevelNumberProvider;
import com.petrolpark.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkLootItemStackNumberProviderTypes {
    
    public static final RegistryEntry<LootItemStackNumberProviderType, LootItemStackNumberProviderType>
    
    COUNT = REGISTRATE.lootItemStackNumberProviderType("count", MapCodec.unit(CountItemStackNumberProvider::new)),
    ENCHANTMENT_LEVEL = REGISTRATE.lootItemStackNumberProviderType("enchantment_level", EnchantmentLevelNumberProvider.CODEC);

    public static final void register() {};

};
