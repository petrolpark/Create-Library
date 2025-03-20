package com.petrolpark;

import com.petrolpark.core.badge.Badge;
import com.petrolpark.core.data.IEntityTarget;
import com.petrolpark.core.shop.Shop;
import com.petrolpark.core.shop.customer.ICustomer;
import com.petrolpark.core.team.ITeam;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class PetrolparkLootContextParams {

    public static final LootContextParam<Badge> BADGE = create("badge");
    public static final LootContextParam<Shop> SHOP = create("shop");
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
