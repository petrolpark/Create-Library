package com.petrolpark.core.shop.customer;

import com.petrolpark.core.shop.Shop;
import com.petrolpark.core.shop.offer.ShopOffer;

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
    public ShopOffer getOpenOffer() {
        return null;
    };

    @Override
    public Shop getShop() {
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
