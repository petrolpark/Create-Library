package com.petrolpark.core.badge;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BadgeDataProvider implements AdvancementGenerator, LootTableSubProvider {

    public BadgeDataProvider() {};
    
    public BadgeDataProvider(HolderLookup.Provider registries) {};

    /**
     * Advancements
     */
    @Override
    @SuppressWarnings("null")
    public void generate(@Nonnull HolderLookup.Provider registries, @Nonnull Consumer<AdvancementHolder> saver, @Nonnull ExistingFileHelper existingFileHelper) {
        PetrolparkRegistries.BADGES.forEach(badge -> 
            saver.accept(new Advancement.Builder()
                .parent(new AdvancementHolder(Petrolpark.asResource("badge_root"), null))
                .display(
                    badge,
                    badge.getName(),
                    badge.getDescription(),
                    null,
                    AdvancementType.TASK,
                    false,
                    false,
                    true
                ).addCriterion("receive", ReceiveBadgeCriterionTrigger.criterion(badge))
                .requirements(AdvancementRequirements.allOf(Collections.singletonList("receive")))
                .rewards(AdvancementRewards.Builder
                    .loot(lootTableId(badge))
                ).build(Petrolpark.asResource("badge/"+badge.id.getPath()))
            )
        );
    };

    /**
     * Loot tables
     */
    @Override
    public void generate(@Nonnull BiConsumer<ResourceKey<LootTable>, Builder> output) {
        PetrolparkRegistries.BADGES.forEach(badge -> 
            output.accept(lootTableId(badge), new LootTable.Builder()
                .withPool(new LootPool.Builder()
                    .add(LootItem.lootTableItem(badge)
                        .apply(BadgeAwardLootItemFunction::new)
                    )
                )
            )
        );
    };

    private ResourceKey<LootTable> lootTableId(Badge badge) {
        return ResourceKey.create(Registries.LOOT_TABLE, Petrolpark.asResource("badge/"+badge.getId().getPath()));
    };
    
};
