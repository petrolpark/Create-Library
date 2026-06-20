package com.petrolpark.core.world.item.restaurant.customer;

import com.petrolpark.core.world.item.restaurant.Restaurant;
import com.petrolpark.core.world.item.restaurant.offer.RestaurantOffer;
import com.petrolpark.registry.PetrolparkLootContextParams;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootParams;

public interface ICustomer {

    public static final int INFINITE_ORDER_TIME = -1;

    public static void addLootParams(ICustomer customer, LootParams.Builder builder) {
        customer.supplyLootParams(builder
            .withParameter(PetrolparkLootContextParams.CUSTOMER, customer)
            .withParameter(PetrolparkLootContextParams.RESTAURANT, customer.getRestaurant())
        );
    };

    public int getOrderTime();

    public int getElapsedOrderTime();
    
    public RestaurantOffer getOpenOffer();

    public Restaurant getRestaurant();

    public void clearOpenOffer();

    public Component getName();

    void supplyLootParams(LootParams.Builder builder);
};
