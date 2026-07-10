package petrolpark.mc.library.core.world.restaurant.customer;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootParams;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;

public interface ICustomer {

    public static final int INFINITE_ORDER_TIME = -1;

    public static void addLootParams(ICustomer customer, LootParams.Builder builder) {
        customer.supplyLootParams(builder
            .withParameter(PetrolparkLootContextParams.CUSTOMER, customer)
            .withParameter(PetrolparkLootContextParams.RESTAURANT, customer.getRestaurant())
        );
    };

    public int getElapsedOrderTime();
    
    public IRestaurantOrder getOpenOrder();

    public Holder<Restaurant> getRestaurant();

    public void clearOpenOrder();

    public Component getName();

    void supplyLootParams(LootParams.Builder builder);
};
