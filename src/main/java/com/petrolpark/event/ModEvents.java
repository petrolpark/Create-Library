package com.petrolpark.event;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.recipe.bogglepattern.BogglePattern;
import com.petrolpark.core.shop.Shop;
import com.petrolpark.core.shop.offer.ShopOfferGenerator;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = Petrolpark.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {
    
    @SubscribeEvent
    public static final void onDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PetrolparkRegistries.Keys.CONTAMINANT, Contaminant.DIRECT_CODEC, Contaminant.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.SHOP, Shop.DIRECT_CODEC, Shop.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.SHOP_OFFER_GENERATOR, ShopOfferGenerator.DIRECT_CODEC, ShopOfferGenerator.DIRECT_CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.BOGGLE_PATTERN, BogglePattern.DIRECT_CODEC, BogglePattern.DIRECT_NETWORK_CODEC);
    };

    @SubscribeEvent
    public static final void onNewRegistry(NewRegistryEvent event) {
        PetrolparkRegistries.init(event);
    }
};
