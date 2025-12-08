package com.petrolpark.core.data.loot.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkLootModifierTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

public record ModifyNestedLootTableModifier(ILootTableModifier modifier) implements ILootPoolSingletonEntryModifier {

    public static final MapCodec<ModifyNestedLootTableModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ILootTableModifier.CODEC.fieldOf("modifier").forGetter(ModifyNestedLootTableModifier::modifier)
    ).apply(instance, ModifyNestedLootTableModifier::new));

    @Override
    public LootPoolEntryContainer modifySingleton(HolderLookup.Provider registries, LootTable lootTable, LootPool pool, LootPoolSingletonContainer entry) {
        if (entry instanceof NestedLootTable nestedLootTable) return nestedLootTable.contents.right().map(lt -> modifier().modify(registries, lt)).map(NestedLootTable::inlineLootTable).map(NestedLootTable.Builder::build).orElseThrow(() -> new IllegalArgumentException("Can only modify inline Loot Tables. To modify referenced Loot Tables, create a separate Loot Table Modifier"));
        throw new IllegalArgumentException("Not a nested Loot Table entry");
    };

    @Override
    public LootPoolEntryModifierType getType() {
        return PetrolparkLootModifierTypes.MODIFY_NESTED_TABLE.get();
    };
    
};
