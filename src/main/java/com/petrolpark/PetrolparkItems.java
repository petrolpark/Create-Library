package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.PetrolparkTags.commonItemTag;

import com.petrolpark.common.item.shulkerbelt.ShulkerBeltItem;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.core.dough.rollingPin.RollingPinItem;
import com.petrolpark.core.item.SharedItem;
import com.petrolpark.core.recipe.book.RecipeBookItem;
import com.petrolpark.core.shop.ShopMenuItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.Tags;

public class PetrolparkItems {

    public static final ItemEntry<RollingPinItem> ROLLING_PIN = REGISTRATE.sharedItem(SharedFeatureFlag.ROLLING_PIN, "rolling_pin", RollingPinItem::new)    
        .properties(p -> p
            .stacksTo(1)
        ).register();
    
    public static final ItemEntry<ShopMenuItem> MENU = REGISTRATE.item("menu", ShopMenuItem::new)
        .defaultModel()
        .register();

    public static final ItemEntry<RecipeBookItem> RECIPE_BOOK = REGISTRATE.item("recipe_book", RecipeBookItem::new)
        .defaultModel()
        .tag(ItemTags.BOOKSHELF_BOOKS)
        .register();

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
        ).defaultModel()
        .tag(Tags.Items.FOODS, commonItemTag("foods/butter"))
        .register(),
    FRIES = REGISTRATE.sharedItem(SharedFeatureFlag.FRIES, "fries", SharedItem::new)
        .properties(p -> p
            .food(PetrolparkFoods.FRIES)
        ).defaultModel()
        .tag(Tags.Items.FOODS, commonItemTag("foods/fries"))
        .register(),
    MASHED_POTATO = REGISTRATE.sharedItem(SharedFeatureFlag.POTATO_PRODUCTS, "mashed_potato", SharedItem::new)
        .properties(p -> p
            .food(PetrolparkFoods.MASHED_POTATO)
        ).tag(Tags.Items.FOODS, commonItemTag("foods/mashed_potato"))
        .defaultModel()
        .register(),
    MESH = REGISTRATE.sharedItem(SharedFeatureFlag.MESH, "mesh", SharedItem::new)
        .defaultModel()
        .register(),
    RAW_FRIES = REGISTRATE.sharedItem(SharedFeatureFlag.FRIES, "raw_fries", SharedItem::new)
        .properties(p -> p
            .food(PetrolparkFoods.RAW_FRIES)
        ).defaultModel()
        .tag(Tags.Items.FOODS)
        .register(),
    EGGSHELL = REGISTRATE.sharedItem(SharedFeatureFlag.EGG_PRODUCTS, "eggshell", SharedItem::new)
        .defaultModel()
        .tag(commonItemTag("eggshell"))
        .register(),
    YOLK = REGISTRATE.sharedItem(SharedFeatureFlag.EGG_PRODUCTS, "yolk", SharedItem::new)
        .defaultModel()
        .properties(p -> p
            .food(PetrolparkFoods.YOLK)
        ).register();
        

    public static final void register() {};
};
