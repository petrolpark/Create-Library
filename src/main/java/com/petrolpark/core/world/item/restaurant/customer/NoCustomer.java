package com.petrolpark.core.world.item.restaurant.customer;

import com.petrolpark.core.world.item.restaurant.Restaurant;
import com.petrolpark.core.world.item.restaurant.offer.RestaurantOffer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootParams.Builder;

public class NoCustomer implements ICustomer {

    public static final NoCustomer INSTANCE = new NoCustomer();

    @Override
    public int getOrderTime() {
        return INFINITE_ORDER_TIME;
    };

    @Override
    public int getElapsedOrderTime() {
        return 0;
    };

    @Override
    public RestaurantOffer getOpenOffer() {
        return null;
    };

    @Override
    public Restaurant getRestaurant() {
        return null;
    };

    @Override
    public void clearOpenOffer() {};

    @Override
    public Component getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    };

    @Override
    public void supplyLootParams(Builder builder) {};
    
};
