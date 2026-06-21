package petrolpark.mc.library.core.data.loot.modifier;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;

public interface ILootPoolSingletonEntryModifier extends ILootPoolEntryModifier {
    
    @Override
    @ApiStatus.NonExtendable
    public default LootPoolEntryContainer modify(HolderLookup.Provider registries, LootTable lootTable, LootPool pool, LootPoolEntryContainer entry) {
        if (entry instanceof LootPoolSingletonContainer singleton) return modifySingleton(registries, lootTable, pool, singleton);
        throw new IllegalArgumentException("Not a Loot Pool Entry singleton");
    };

    public LootPoolEntryContainer modifySingleton(HolderLookup.Provider registries, LootTable lootTable, LootPool pool, LootPoolSingletonContainer entry);
};
