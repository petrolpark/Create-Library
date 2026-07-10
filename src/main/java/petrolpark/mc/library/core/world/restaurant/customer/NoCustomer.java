package petrolpark.mc.library.core.world.restaurant.customer;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;

public class NoCustomer implements ICustomer {

    public static final NoCustomer INSTANCE = new NoCustomer();

    @Override
    public int getElapsedOrderTime() {
        return 0;
    };

    @Override
    public IRestaurantOrder getOpenOrder() {
        return null;
    };

    @Override
    public Holder<Restaurant> getRestaurant() {
        return null;
    };

    @Override
    public void clearOpenOrder() {};

    @Override
    public Component getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    };

    @Override
    public void supplyLootParams(Builder builder) {};
    
};
