package com.petrolpark;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class PetrolparkBuiltInLootTables {
  
    public static final ResourceKey<LootTable> EGG = ResourceKey.create(Registries.LOOT_TABLE, Petrolpark.asResource("gameplay/egg"));
};
