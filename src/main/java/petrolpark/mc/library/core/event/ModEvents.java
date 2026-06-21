package petrolpark.mc.library.core.event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.client.effectShaders.IShaderEffect;
import petrolpark.mc.library.core.client.effectShaders.ShaderEffectReloadHandler;
import petrolpark.mc.library.core.data.loot.modifier.LootTableModification;
import petrolpark.mc.library.core.data.recipe.bogglePattern.BogglePattern;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.world.item.restaurant.Restaurant;
import petrolpark.mc.library.core.world.item.restaurant.offer.RestaurantOfferGenerator;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Petrolpark.MOD_ID)
public class ModEvents {
    
    @SubscribeEvent
    public static final void onDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PetrolparkRegistries.Keys.FLAG, Flag.DIRECT_CODEC, Flag.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.LOOT_TABLE_MODIFICATION, LootTableModification.DIRECT_CODEC, LootTableModification.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.RESTAURANT, Restaurant.DIRECT_CODEC, Restaurant.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.RESTAURANT_OFFER_GENERATOR, RestaurantOfferGenerator.DIRECT_CODEC, RestaurantOfferGenerator.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.BOGGLE_PATTERN, BogglePattern.DIRECT_CODEC, BogglePattern.DIRECT_NETWORK_CODEC);
    };

    /**
     * Caches shader effects on reload (avoid lag spikes).
     * @param event
     */
    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new PreparableReloadListener() {
            @Override
            @ParametersAreNonnullByDefault
            public @Nonnull CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
                return CompletableFuture.runAsync(() -> {
                    ShaderEffectReloadHandler.clearCache();

                    Minecraft mc = Minecraft.getInstance();

                    for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
                        if (effect instanceof IShaderEffect shaderEffect ) {
                            ResourceLocation location = shaderEffect.getShader();
                            if (location != null && !ShaderEffectReloadHandler.hasShader(shaderEffect)) {
                                ShaderEffectReloadHandler.createShader(shaderEffect, mc, pResourceManager);
                            };
                        };
                    };

                    Petrolpark.LOGGER.info("All shader effects preloaded.");
                }, pGameExecutor).thenCompose(pPreparationBarrier::wait);
            }
        });
    }
};
