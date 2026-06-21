package petrolpark.mc.library.core.data.loot.provider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

public class ConditionalLootTableProvider implements DataProvider {

    public static final Codec<Optional<WithConditions<LootTable>>> CONDITIONAL_LOOT_TABLE_CODEC = ConditionalOps.createConditionalCodecWithConditions(LootTable.DIRECT_CODEC);
    protected static final ResourceKey<Registry<Optional<WithConditions<LootTable>>>> CONDITIONAL_LOOT_TABLE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("loot_table"));

    protected final PackOutput.PathProvider pathProvider;
    protected final List<ConditionalLootTableProvider.SubProviderEntry> subProviders;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    public ConditionalLootTableProvider(PackOutput output, List<ConditionalLootTableProvider.SubProviderEntry> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
        this.subProviders = subProviders;
        this.registries = registries;
    };

    @Override
    public final CompletableFuture<?> run(@Nonnull CachedOutput output) {
        return registries.thenCompose(registries -> run(output, registries));
    };

    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        final WritableRegistry<Optional<WithConditions<LootTable>>> conditionalLootTableRegistry = new MappedRegistry<>(CONDITIONAL_LOOT_TABLE_REGISTRY_KEY, Lifecycle.experimental());
        getSubProviders().forEach(subProviderEntry -> subProviderEntry.provider().apply(provider).generate((id, lootTableBuilder, conditions) -> {
            lootTableBuilder.setRandomSequence(id);
            conditionalLootTableRegistry.register(ResourceKey.create(CONDITIONAL_LOOT_TABLE_REGISTRY_KEY, id), Optional.of(new WithConditions<>(conditions, lootTableBuilder.setParamSet(subProviderEntry.paramSet()).build())), RegistrationInfo.BUILT_IN);
        }));
        conditionalLootTableRegistry.freeze();

        return CompletableFuture.allOf(conditionalLootTableRegistry.entrySet().stream().map(entry -> {
            return DataProvider.saveStable(output, provider, CONDITIONAL_LOOT_TABLE_CODEC, entry.getValue(), pathProvider.json(entry.getKey().location()));
        }).toArray(CompletableFuture[]::new));
    };

    public List<ConditionalLootTableProvider.SubProviderEntry> getSubProviders() {
        return subProviders;
    };

    @Override
    public final String getName() {
        return "Conditional Loot Tables";
    };

    public static record SubProviderEntry(Function<HolderLookup.Provider, ConditionalLootTableSubProvider> provider, LootContextParamSet paramSet) {};
    
};
