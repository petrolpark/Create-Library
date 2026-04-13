package com.petrolpark;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.petrolpark.compat.Mods;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.badge.BadgeDataProvider;
import com.petrolpark.core.registrate.PetrolparkRegistrateTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class PetrolparkDatagen {

    public static final void prepareDatagen() {
        for (SharedFeatureFlag flag : SharedFeatureFlag.values()) flag.enable(Mods.PETROLPARK); // All must be enabled for Datagen
        PetrolparkRegistrateTags.addGenerators();
    };

    public static final void gatherData(GatherDataEvent event) {

        final DataGenerator generator = event.getGenerator();
        final PackOutput output = generator.getPackOutput();
		final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, existingFileHelper, Collections.singletonList(new BadgeDataProvider())));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(BadgeDataProvider::new, LootContextParamSets.ADVANCEMENT_REWARD)), lookupProvider));
    };
};
