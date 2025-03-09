package com.petrolpark.event;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.shop.Shop;
import com.petrolpark.shop.offer.ShopOfferGenerator;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Petrolpark.MOD_ID)
public class ModEvents {
    
    @SubscribeEvent
    public static void addDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PetrolparkRegistries.Keys.CONTAMINANT, Contaminant.CODEC, Contaminant.CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.SHOP, Shop.CODEC, Shop.CODEC);
        event.dataPackRegistry(PetrolparkRegistries.Keys.SHOP_OFFER_GENERATOR, ShopOfferGenerator.CODEC, ShopOfferGenerator.CODEC);
    };
};
