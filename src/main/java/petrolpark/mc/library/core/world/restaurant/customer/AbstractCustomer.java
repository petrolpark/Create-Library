package petrolpark.mc.library.core.world.restaurant.customer;

import javax.annotation.Nonnull;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;

public abstract class AbstractCustomer implements ICustomer, INBTSerializable<CompoundTag> {

    protected int elapsedOrderTime = 0;

    protected IRestaurantOrder openOrder = null;
    protected Holder<Restaurant> restaurant = null;

    @Override
    public int getElapsedOrderTime() {
        return elapsedOrderTime;
    };

    @Override
    public IRestaurantOrder getOpenOrder() {
        return openOrder;
    };

    @Override
    public Holder<Restaurant> getRestaurant() {
        return restaurant;
    };

    @Override
    public void clearOpenOrder() {
        elapsedOrderTime = 0;
        openOrder = null;
        restaurant = null;
    };

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        // if (getOrderTime() != INFINITE_ORDER_TIME) {
        //     tag.putInt("OrderTime", orderTime);
        //     if (elapsedOrderTime > 0) tag.putInt("Elapsed", elapsedOrderTime);
        // };
        // if (openOrder != null) RestaurantOffer.CODEC.encodeStart(NbtOps.INSTANCE, openOrder)
        //     .resultOrPartial(Petrolpark.LOGGER::warn)
        //     .ifPresent(t -> tag.put("Offer", t));
        // if (restaurant != null) tag.put("Restaurant", Restaurant.CODEC.encodeStart(NbtOps.INSTANCE, restaurant).getOrThrow());
        return tag;
    };

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        // orderTime = INFINITE_ORDER_TIME;
        // if (nbt.contains("OrderTime", Tag.TAG_INT)) {
        //     orderTime = nbt.getInt("OrderTime");
        //     if (nbt.contains("Elapsed", Tag.TAG_INT)) elapsedOrderTime = nbt.getInt("Elapsed");
        // };
        // if (nbt.contains("Offer", Tag.TAG_COMPOUND)) RestaurantOffer.CODEC.decode(NbtOps.INSTANCE, nbt.get("Offer"))
        //     .resultOrPartial(Petrolpark.LOGGER::warn)
        //     .map(Pair::getFirst)
        //     .ifPresent(s -> openOrder = s);
        // if (nbt.contains("Restaurant", Tag.TAG_STRING)) restaurant = Restaurant.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("Restaurant")).resultOrPartial(Petrolpark.LOGGER::warn).get();
    };

    public void tick() {
        //if (elapsedOrderTime < orderTime && orderTime != INFINITE_ORDER_TIME) elapsedOrderTime++;
    };
    
};
