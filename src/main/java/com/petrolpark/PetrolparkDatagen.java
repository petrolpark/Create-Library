package com.petrolpark;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.petrolpark.badge.BadgeDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class PetrolparkDatagen {

    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, existingFileHelper, Collections.singletonList(new BadgeDataProvider())));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(BadgeDataProvider::new, LootContextParamSets.ADVANCEMENT_REWARD)), lookupProvider));
    };
};
