package com.petrolpark.core.data.loot;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class UnvalidatedLootTableProvider extends LootTableProvider {

    public static final void add(GatherDataEvent event, Stream<Function<HolderLookup.Provider, LootTableSubProvider>> subProviders) {
        event.addProvider(new UnvalidatedLootTableProvider(event, subProviders));
    };

    public UnvalidatedLootTableProvider(GatherDataEvent event, Stream<Function<HolderLookup.Provider, LootTableSubProvider>> subProviders) {
        this(event.getGenerator().getPackOutput(), subProviders, event.getLookupProvider());
    };
    
    public UnvalidatedLootTableProvider(PackOutput output, Stream<Function<HolderLookup.Provider, LootTableSubProvider>> subProviders, CompletableFuture<Provider> registries) {
        super(output, Collections.emptySet(), subProviders.map(provider -> new SubProviderEntry(provider, LootContextParamSets.EMPTY)).toList(), registries);
    };

    @Override
    @Deprecated
    protected void validate(@Nonnull WritableRegistry<LootTable> writableregistry, @Nonnull ValidationContext validationcontext, @Nonnull ProblemReporter.Collector problemreporter) {
        //NOOP
    };
};
