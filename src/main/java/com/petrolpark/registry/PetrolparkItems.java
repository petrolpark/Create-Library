package com.petrolpark.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.world.item.crafting.recipeBook.RecipeBookItem;
import com.petrolpark.core.world.item.restaurant.RestaurantMenuItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;

public class PetrolparkItems {
    
    public static final ItemEntry<RestaurantMenuItem> MENU = REGISTRATE.item("menu", RestaurantMenuItem::new)
        .defaultModel()
        .register();

    public static final ItemEntry<RecipeBookItem> RECIPE_BOOK = REGISTRATE.item("recipe_book", RecipeBookItem::new)
        .defaultModel()
        .tag(ItemTags.BOOKSHELF_BOOKS)
        .register();

    public static final void register() {};
};
