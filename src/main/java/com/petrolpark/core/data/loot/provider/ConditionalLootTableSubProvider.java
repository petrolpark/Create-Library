package com.petrolpark.core.data.loot.provider;

import java.util.List;

import org.apache.commons.lang3.function.TriConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.conditions.ICondition;

@FunctionalInterface
public interface ConditionalLootTableSubProvider {
    
    void generate(TriConsumer<ResourceLocation, LootTable.Builder, List<ICondition>> output);
};
