package com.petrolpark.core.data.loot.modifier;

import com.mojang.serialization.Codec;
import com.petrolpark.registry.PetrolparkRegistries;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public interface ILootPoolEntryModifier extends ILootModifierBase {
    
        /**
     * Use {@link ILootPoolEntryModifier#CODEC instead}.
     */
    static final Codec<ILootPoolEntryModifier> TYPED_CODEC = PetrolparkRegistries.LOOT_POOL_ENTRY_MODIFIER_TYPES
        .byNameCodec()
        .dispatch(ILootPoolEntryModifier::getType, LootPoolEntryModifierType::codec);

    public static final Codec<ILootPoolEntryModifier> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);
    
    public LootPoolEntryContainer modify(HolderLookup.Provider registries, LootTable lootTable, LootPool pool, LootPoolEntryContainer entry);

    public LootPoolEntryModifierType getType();
};
