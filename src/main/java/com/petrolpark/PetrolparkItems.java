package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.SharedItem;
import com.petrolpark.core.shop.ShopMenuItem;
import com.tterrag.registrate.util.entry.ItemEntry;

public class PetrolparkItems {
    
    public static final ItemEntry<ShopMenuItem> MENU = REGISTRATE.item("menu", ShopMenuItem::new).register();

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
