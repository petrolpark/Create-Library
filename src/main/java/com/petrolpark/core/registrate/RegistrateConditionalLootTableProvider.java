package com.petrolpark.core.registrate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.function.TriFunction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.petrolpark.core.data.loot.provider.ConditionalLootTableProvider;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider.LootType;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.LogicalSide;

public class RegistrateConditionalLootTableProvider extends ConditionalLootTableProvider implements RegistrateProvider {

    public static final ProviderType<RegistrateConditionalLootTableProvider> TYPE = ProviderType.registerServerData("conditional_loot", RegistrateConditionalLootTableProvider::new);
    
    protected final AbstractRegistrate<?> parent;
    protected final Multimap<ConditionalLootType<?>, Consumer<? super RegistrateConditionalLootTableSubProvider>> specialLootActions = HashMultimap.create();

    public RegistrateConditionalLootTableProvider(AbstractRegistrate<?> parent, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, Collections.emptyList(), registriesFuture);
        this.parent = parent;
    };

    @SuppressWarnings("unchecked")
    public <T extends RegistrateConditionalLootTableSubProvider> void addLootAction(ConditionalLootType<T> type, NonNullConsumer<T> action) {
        specialLootActions.put(type, (Consumer<RegistrateConditionalLootTableSubProvider>) action);
    };

    @Override
    protected CompletableFuture<?> run(CachedOutput output, Provider provider) {
        parent.genData(TYPE, this);
        return super.run(output, provider);
    };

    @Override
    public List<SubProviderEntry> getSubProviders() {
        return LOOT_TYPES.values().stream().map(type ->
            new SubProviderEntry(provider ->
                type.getLootCreator(provider, parent, cons ->
                    specialLootActions.get(type).forEach(c ->
                        c.accept(cons)
                    )
                ), type.getLootSet()
            )
        ).toList();
    };

    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    };

    public interface ConditionalLootType<T extends RegistrateConditionalLootTableSubProvider> extends LootType<T> {

        public static ConditionalLootType<RegistrateConditionalBlockLootTableSubProvider> BLOCK = register("block", LootContextParamSets.BLOCK, RegistrateConditionalBlockLootTableSubProvider::new);
        //static ConditionalLootType<RegistrateEntityLootTables> ENTITY = register("entity", LootContextParamSets.ENTITY, RegistrateEntityLootTables::new);

        public static <SUB_PROVIDER extends RegistrateConditionalLootTableSubProvider> ConditionalLootType<SUB_PROVIDER> register(String name, LootContextParamSet set, TriFunction<HolderLookup.Provider, AbstractRegistrate<?>, Consumer<SUB_PROVIDER>, SUB_PROVIDER> factory) {
            
            final ConditionalLootType<SUB_PROVIDER> type = new ConditionalLootType<>() {
                
                @Override
                public SUB_PROVIDER getLootCreator(@Nonnull HolderLookup.Provider provider, @Nonnull AbstractRegistrate<?> parent, @Nonnull Consumer<SUB_PROVIDER> callback) {
                    return factory.apply(provider, parent, callback);
                };

                @Override
                public LootContextParamSet getLootSet() {
                    return set;
                };
            };

            LOOT_TYPES.put(name, type);
            return type;
        };
    };

    protected static final Map<String, ConditionalLootType<?>> LOOT_TYPES = new HashMap<>();
};
