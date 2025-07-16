package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.common.item.shulkerbelt.ShulkerBeltItem;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.SharedItem;
import com.petrolpark.core.shop.ShopMenuItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Rarity;

public class PetrolparkItems {
    
    public static final ItemEntry<ShopMenuItem> MENU = REGISTRATE.item("menu", ShopMenuItem::new).register();

    public static final ItemEntry<ShulkerBeltItem> SHULKER_BELT = REGISTRATE.item("shulker_belt", ShulkerBeltItem::new)
        .properties(p -> p
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)
            .requiredFeatures(PetrolparkFeatureFlags.EXTENDED_INVENTORY.featureFlag)
        ).register();

    // Shared Features

    public static final ItemEntry<SharedItem>

    BUTTER = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "butter", SharedItem::new)
        .properties(p -> p
            .food(PetrolparkFoods.BUTTER)
        ).register(),
    MESH = REGISTRATE.sharedItem(SharedFeatureFlag.MESH, "mesh", SharedItem::new)
        .register();
        

    public static final void register() {};
};
