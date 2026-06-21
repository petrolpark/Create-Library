package petrolpark.mc.library.shared.registry;

import petrolpark.mc.library.Petrolpark;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class SharedLootTables {
  
    public static final ResourceKey<LootTable> EGG = ResourceKey.create(Registries.LOOT_TABLE, Petrolpark.asResource("gameplay/egg"));
};
