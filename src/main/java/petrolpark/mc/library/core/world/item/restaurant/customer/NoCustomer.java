package petrolpark.mc.library.core.world.item.restaurant.customer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import petrolpark.mc.library.core.world.item.restaurant.Restaurant;
import petrolpark.mc.library.core.world.item.restaurant.order.RestaurantOffer;

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
