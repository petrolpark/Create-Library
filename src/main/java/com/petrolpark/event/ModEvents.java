package com.petrolpark.event;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.shadereffects.IShaderEffect;
import com.petrolpark.shadereffects.ShaderEffectReloadHandler;
import com.petrolpark.shop.Shop;
import com.petrolpark.shop.offer.ShopOfferGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Petrolpark.MOD_ID)
public class ModEvents {
    
    @SubscribeEvent
    public static void addDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PetrolparkRegistries.Keys.CONTAMINANT, Contaminant.CODEC, Contaminant.CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.SHOP, Shop.CODEC, Shop.CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.SHOP_OFFER_GENERATOR, ShopOfferGenerator.CODEC, ShopOfferGenerator.CODEC);
    };

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new PreparableReloadListener() {
            @Override
            public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
                return CompletableFuture.runAsync(() -> {
                    ShaderEffectReloadHandler.clearCache();

                    Minecraft mc = Minecraft.getInstance();

                    for ( MobEffect effect : ForgeRegistries.MOB_EFFECTS.getValues() ) {
                        if (effect instanceof IShaderEffect shaderEffect ) {
                            ResourceLocation location = shaderEffect.getShader();
                            if (location != null && !ShaderEffectReloadHandler.hasShader(shaderEffect)) {
                                ShaderEffectReloadHandler.createShader(shaderEffect, mc, pResourceManager);
                            }
                        }
                    }

                    System.out.println("[Petrolpark] All shader effects preloaded.");
                }, pGameExecutor).thenCompose(pPreparationBarrier::wait);
            }
        });
    }
};
