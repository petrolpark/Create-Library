package com.petrolpark.core.data.loot.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.registry.PetrolparkLootModifierTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public record AddEntryLootPoolModifier(Either<String, Integer> poolIdentifier, LootPoolEntryContainer entryContainer) implements ILootPoolModifier {

    public static final MapCodec<AddEntryLootPoolModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ILootPoolModifier.poolField(instance).t1(),
        LootPoolEntries.CODEC.fieldOf("entry").forGetter(AddEntryLootPoolModifier::entryContainer)
    ).apply(instance, AddEntryLootPoolModifier::new));

    @Override
    public LootPool.Builder modifyPool(HolderLookup.Provider registries, LootTable lootTable, LootPool pool) {
        return add(copy(pool), entryContainer());
    };

    @Override
    public LootTableModifierType getType() {
        return PetrolparkLootModifierTypes.ADD_ENTRY.get();
    };


    
};
