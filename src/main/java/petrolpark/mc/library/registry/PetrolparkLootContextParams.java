package petrolpark.mc.library.registry;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.badge.Badge;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.item.restaurant.Restaurant;
import petrolpark.mc.library.core.world.item.restaurant.customer.ICustomer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class PetrolparkLootContextParams {

    public static final LootContextParam<Badge> BADGE = create("badge");
    public static final LootContextParam<Restaurant> RESTAURANT = create("restaurant");
    public static final LootContextParam<ICustomer> CUSTOMER = create("customer");
    public static final LootContextParam<Entity> CUSTOMER_ENTITY = createEntity("customer_entity");

    public static final LootContextParam<ITeam> TEAM = create("team");

    private static <E extends Entity> LootContextParam<E> createEntity(String id) {
        LootContextParam<E> param = create(id);
        IEntityTarget.register(param);
        return param;
    };
  
    private static <T> LootContextParam<T> create(String id) {
        return new LootContextParam<>(Petrolpark.asResource(id));
    };
};
