package com.petrolpark.core.data.loot.modifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber
public class LootTableModificationManager {

    private static final PreparableReloadListener RELOAD_LISTENER = new PreparableReloadListener() {

        @Override
        public CompletableFuture<Void> reload(@Nonnull PreparationBarrier preparationBarrier, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller preparationsProfiler, @Nonnull ProfilerFiller reloadProfiler, @Nonnull Executor backgroundExecutor, @Nonnull Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> {
                LOOT_TABLE_MODIFICATIONS.clear();
                finishedRegistration = false;
            }, gameExecutor).thenCompose(preparationBarrier::wait);
        };
        
    };

    private static final Multimap<ResourceLocation, LootTableModification> LOOT_TABLE_MODIFICATIONS = MultimapBuilder.hashKeys().arrayListValues().build();
    private static boolean finishedRegistration = false;

    public static final LootTableModification register(LootTableModification modification) {
        if (finishedRegistration) throw new IllegalStateException("Too late to register Loot Table Modification");
        LOOT_TABLE_MODIFICATIONS.put(modification.target(), modification);
        return modification;
    };

    @SubscribeEvent
    public static final void onLootTableLoad(LootTableLoadEvent event) {
        finishedRegistration = true; // If we have started modifying loot tables, its too late to register more modifications
        final ICondition.IContext context = ICondition.IContext.TAGS_INVALID; // Loaded before tags
        final List<ILootTableModifier> modifiers = LOOT_TABLE_MODIFICATIONS.get(event.getName()).stream()
            .filter(modification -> modification.conditions().stream().allMatch(condition -> condition.test(context)))
            .sorted()
            .map(LootTableModification::modifiers)
            .flatMap(List::stream).toList();
        if (!modifiers.isEmpty()) {
            LootTable table = event.getTable();
            for (ILootTableModifier modifier : modifiers) table = modifier.modify(event.getRegistries(), table);
            event.setTable(table);
        };
    };

    @SubscribeEvent
    public static final void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(RELOAD_LISTENER);
    };
    
};
