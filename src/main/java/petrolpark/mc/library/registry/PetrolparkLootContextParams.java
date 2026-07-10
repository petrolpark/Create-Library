package petrolpark.mc.library.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.neoforged.neoforge.items.IItemHandler;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.badge.Badge;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;

public class PetrolparkLootContextParams {

    // Generic
    public static final LootContextParam<Badge> BADGE = create("badge");
    public static final LootContextParam<ITeam> TEAM = create("team");
    public static final LootContextParam<IItemHandler> ITEM_HANDLER = create("inventory");

    // Restaurant
    public static final LootContextParam<Holder<Restaurant>> RESTAURANT = create("restaurant");
    public static final LootContextParam<Integer> RESTAURANT_LEVEL = create("restaurant_level");
    public static final LootContextParam<ICustomer> CUSTOMER = create("customer");
    public static final LootContextParam<Entity> CUSTOMER_ENTITY = createEntity("customer_entity");

    private static <E extends Entity> LootContextParam<E> createEntity(String id) {
        LootContextParam<E> param = create(id);
        IEntityTarget.register(param);
        return param;
    };
  
    private static <T> LootContextParam<T> create(String id) {
        return new LootContextParam<>(Petrolpark.asResource(id));
    };
};
