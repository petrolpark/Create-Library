package com.petrolpark.core.registrate.dataGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.function.TriConsumer;

import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.neoforged.neoforge.common.conditions.ICondition;

public class RegistrateConditionalBlockLootTableSubProvider extends RegistrateBlockLootTables implements RegistrateConditionalLootTableSubProvider {

    protected final Map<ResourceLocation, Pair<LootTable.Builder, List<ICondition>>> conditionalTables = new HashMap<>();

    protected final List<Block> unconditionalBlocks = new ArrayList<>();
    protected List<ICondition> nextConditions = null;

    public RegistrateConditionalBlockLootTableSubProvider(HolderLookup.Provider registries, AbstractRegistrate<?> parent, Consumer<RegistrateConditionalBlockLootTableSubProvider> callback) {
        super(registries, parent, prov -> callback.accept((RegistrateConditionalBlockLootTableSubProvider)prov));
    };

    @Override
    public void generate(TriConsumer<ResourceLocation, Builder, List<ICondition>> output) {
        RegistrateConditionalLootTableSubProvider.super.generate(output);
        conditionalTables.forEach((rl, pair) -> output.accept(rl, pair.getFirst(), pair.getSecond()));
    };

    @Override
    protected void generate() {
        super.generate();
        if (nextConditions != null) throw new IllegalStateException("Unused Conditions");
    };

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return unconditionalBlocks;
    };

    public void add(@Nonnull Block block, @Nonnull LootTable.Builder lootTableBuilder, List<ICondition> conditions) {
        conditionalTables.put(block.getLootTable().location(), Pair.of(lootTableBuilder, conditions));
    };

    /**
     * Applies the given {@link ICondition}s to the next Block {@link RegistrateConditionalBlockLootTableSubProvider#add(Block, Builder) added}.
     * If no further Block is added, an error will be thrown.
     * @param conditions
     */
    public RegistrateConditionalBlockLootTableSubProvider withConditions(List<ICondition> conditions) {
        nextConditions = conditions;
        return this;
    };

    @Override
    public void add(@Nonnull Block block, @Nonnull LootTable.Builder lootTableBuilder) {
        if (nextConditions != null) {
            add(block, lootTableBuilder, nextConditions);
            nextConditions = null;
        } else {
            super.add(block, lootTableBuilder);
            unconditionalBlocks.add(block);
        };
    };
    
};
