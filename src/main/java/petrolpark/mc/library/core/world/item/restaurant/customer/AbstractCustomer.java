package petrolpark.mc.library.core.world.item.restaurant.customer;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.item.restaurant.Restaurant;
import petrolpark.mc.library.core.world.item.restaurant.offer.RestaurantOffer;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public abstract class AbstractCustomer implements ICustomer, INBTSerializable<CompoundTag> {

    public int orderTime = INFINITE_ORDER_TIME;
    protected int elapsedOrderTime = 0;

    protected RestaurantOffer openOffer = null;
    protected Holder<Restaurant> restaurant = null;

    @Override
    public int getOrderTime() {
        return orderTime;
    };

    @Override
    public int getElapsedOrderTime() {
        return elapsedOrderTime;
    };

    @Override
    public RestaurantOffer getOpenOffer() {
        return openOffer;
    };

    @Override
    public Restaurant getRestaurant() {
        return restaurant.value();
    };

    @Override
    public void clearOpenOffer() {
        orderTime = INFINITE_ORDER_TIME;
        elapsedOrderTime = 0;
        openOffer = null;
        restaurant = null;
    };

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (getOrderTime() != INFINITE_ORDER_TIME) {
            tag.putInt("OrderTime", orderTime);
            if (elapsedOrderTime > 0) tag.putInt("Elapsed", elapsedOrderTime);
        };
        if (openOffer != null) RestaurantOffer.CODEC.encodeStart(NbtOps.INSTANCE, openOffer)
            .resultOrPartial(Petrolpark.LOGGER::warn)
            .ifPresent(t -> tag.put("Offer", t));
        if (restaurant != null) tag.put("Restaurant", Restaurant.CODEC.encodeStart(NbtOps.INSTANCE, restaurant).getOrThrow());
        return tag;
    };

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        orderTime = INFINITE_ORDER_TIME;
        if (nbt.contains("OrderTime", Tag.TAG_INT)) {
            orderTime = nbt.getInt("OrderTime");
            if (nbt.contains("Elapsed", Tag.TAG_INT)) elapsedOrderTime = nbt.getInt("Elapsed");
        };
        if (nbt.contains("Offer", Tag.TAG_COMPOUND)) RestaurantOffer.CODEC.decode(NbtOps.INSTANCE, nbt.get("Offer"))
            .resultOrPartial(Petrolpark.LOGGER::warn)
            .map(Pair::getFirst)
            .ifPresent(s -> openOffer = s);
        if (nbt.contains("Restaurant", Tag.TAG_STRING)) restaurant = Restaurant.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("Restaurant")).resultOrPartial(Petrolpark.LOGGER::warn).get();
    };

    public void tick() {
        if (elapsedOrderTime < orderTime && orderTime != INFINITE_ORDER_TIME) elapsedOrderTime++;
    };
    
};
