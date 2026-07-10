package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class PetrolparkLootContextParamSets {
    
    public static final LootContextParamSet ANIMAL_HAPPINESS = REGISTRATE.lootContextParamSet("animal_happiness", builder -> builder
        .required(LootContextParams.ORIGIN)
        .required(LootContextParams.THIS_ENTITY)
    );

    public static final LootContextParamSet RESTAURANT_ORDER_GENERATION = REGISTRATE.lootContextParamSet("restaurant_order_generation", builder -> builder
        .required(PetrolparkLootContextParams.RESTAURANT)
        .required(PetrolparkLootContextParams.TEAM)
        .required(LootContextParams.THIS_ENTITY) // Player taking the order
        .optional(PetrolparkLootContextParams.CUSTOMER_ENTITY) // Entity making the order
    );

    public static final LootContextParamSet RESTAURANT_ORDER_REWARDS = REGISTRATE.lootContextParamSet("restaurant_order_rewwards", builder -> builder
        .required(PetrolparkLootContextParams.RESTAURANT)
        .required(PetrolparkLootContextParams.TEAM)
        .required(PetrolparkLootContextParams.CUSTOMER)
        .required(PetrolparkLootContextParams.ITEM_HANDLER) // Place for reward items to go
        .optional(LootContextParams.THIS_ENTITY) // Player who fulfilled the order
        .optional(PetrolparkLootContextParams.CUSTOMER_ENTITY) // Entity who made the order
    );

    public static final LootContextParamSet ADVANCEMENT_RESTAURANT = REGISTRATE.lootContextParamSet("advancement_restaurant", builder -> builder
        .required(LootContextParams.THIS_ENTITY)
        .required(LootContextParams.ORIGIN)
        .required(PetrolparkLootContextParams.RESTAURANT)
        .required(PetrolparkLootContextParams.TEAM)
    );

    public static final LootContextParamSet RESTAURANT_XP_CALCULATION = REGISTRATE.lootContextParamSet("restaurant_xp_calculation", builder -> builder
        .required(PetrolparkLootContextParams.RESTAURANT_LEVEL)
    );

    public static final void register() {};
};
