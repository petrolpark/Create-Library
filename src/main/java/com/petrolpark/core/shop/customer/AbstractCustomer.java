package com.petrolpark.core.shop.customer;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;
import com.petrolpark.Petrolpark;
import com.petrolpark.core.shop.Shop;
import com.petrolpark.core.shop.offer.ShopOffer;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public abstract class AbstractCustomer implements ICustomer, INBTSerializable<CompoundTag> {

    public int orderTime = INFINITE_ORDER_TIME;
    protected int elapsedOrderTime = 0;

    protected ShopOffer openOffer = null;
    protected Holder<Shop> shop = null;

    @Override
    public int getOrderTime() {
        return orderTime;
    };

    @Override
    public int getElapsedOrderTime() {
        return elapsedOrderTime;
    };

    @Override
    public ShopOffer getOpenOffer() {
        return openOffer;
    };

    @Override
    public Shop getShop() {
        return shop.value();
    };

    @Override
    public void clearOpenOffer() {
        orderTime = INFINITE_ORDER_TIME;
        elapsedOrderTime = 0;
        openOffer = null;
        shop = null;
    };

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (getOrderTime() != INFINITE_ORDER_TIME) {
            tag.putInt("OrderTime", orderTime);
            if (elapsedOrderTime > 0) tag.putInt("Elapsed", elapsedOrderTime);
        };
        if (openOffer != null) ShopOffer.CODEC.encodeStart(NbtOps.INSTANCE, openOffer)
            .resultOrPartial(Petrolpark.LOGGER::warn)
            .ifPresent(t -> tag.put("Offer", t));
        if (shop != null) tag.put("Shop", Shop.CODEC.encodeStart(NbtOps.INSTANCE, shop).getOrThrow());
        return tag;
    };

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        orderTime = INFINITE_ORDER_TIME;
        if (nbt.contains("OrderTime", Tag.TAG_INT)) {
            orderTime = nbt.getInt("OrderTime");
            if (nbt.contains("Elapsed", Tag.TAG_INT)) elapsedOrderTime = nbt.getInt("Elapsed");
        };
        if (nbt.contains("Offer", Tag.TAG_COMPOUND)) ShopOffer.CODEC.decode(NbtOps.INSTANCE, nbt.get("Offer"))
            .resultOrPartial(Petrolpark.LOGGER::warn)
            .map(Pair::getFirst)
            .ifPresent(s -> openOffer = s);
        if (nbt.contains("Shop", Tag.TAG_STRING)) shop = Shop.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("Shop")).resultOrPartial(Petrolpark.LOGGER::warn).get();
    };

    public void tick() {
        if (elapsedOrderTime < orderTime && orderTime != INFINITE_ORDER_TIME) elapsedOrderTime++;
    };
    
};
