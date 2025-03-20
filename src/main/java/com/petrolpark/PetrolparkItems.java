package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.shop.ShopMenuItem;
import com.tterrag.registrate.util.entry.ItemEntry;

public class PetrolparkItems {
    
    public static final ItemEntry<ShopMenuItem> MENU = REGISTRATE.item("menu", ShopMenuItem::new).register();

    public static final void register() {};
};
