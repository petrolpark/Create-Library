package com.petrolpark.core.data.loot.modifier;

import java.util.List;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ILootModifierBase {
    
    public default Optional<ResourceLocation> getRandomSequence(LootTable lootTable) {
        return lootTable.randomSequence;
    };

    public default List<LootPool> getPools(LootTable lootTable) {
        return lootTable.pools;
    };

    public default List<LootItemFunction> getFunctions(LootTable lootTable) {
        return lootTable.functions;
    };

    public default LootTable.Builder withPool(LootTable.Builder builder, LootPool ... pools) {
        builder.pools.add(pools);
        return builder;
    };

    public default LootTable.Builder apply(LootTable.Builder builder, LootItemFunction ... functions) {
        builder.functions.add(functions);
        return builder;
    };

    public default LootTable.Builder copy(LootTable table) {
        final LootTable.Builder builder = LootTable.lootTable()
            .setParamSet(table.getParamSet());
        getRandomSequence(table).ifPresent(builder::setRandomSequence);
        for (LootPool pool : getPools(table)) withPool(builder, pool);
        for (LootItemFunction function : getFunctions(table)) apply(builder, function);
        return builder;
    };

    public default List<LootPoolEntryContainer> getEntries(LootPool pool) {
        return pool.entries;
    };

    public default List<LootItemCondition> getConditions(LootPool pool) {
        return pool.conditions;
    };

    public default List<LootItemFunction> getFunctions(LootPool pool) {
        return pool.functions;
    };

    public default LootPool.Builder add(LootPool.Builder builder, LootPoolEntryContainer ... entries) {
        builder.entries.add(entries);
        return builder;
    };

    public default LootPool.Builder when(LootPool.Builder builder, LootItemCondition ... conditions) {
        builder.conditions.add(conditions);
        return builder;
    };

    public default LootPool.Builder apply(LootPool.Builder builder, LootItemFunction ... functions) {
        builder.functions.add(functions);
        return builder;
    };

    public default LootPool.Builder copy(LootPool pool) {
        final LootPool.Builder builder = LootPool.lootPool()
            .setRolls(pool.getRolls())
            .setBonusRolls(pool.getBonusRolls());
        if (pool.getName() != null) builder.name(pool.getName());
        for (LootPoolEntryContainer entry : getEntries(pool)) add(builder, entry);
        for (LootItemCondition condition : getConditions(pool)) when(builder, condition);
        for (LootItemFunction function : getFunctions(pool)) apply(builder, function);
        return builder;
    };
};
