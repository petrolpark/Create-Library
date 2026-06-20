package com.petrolpark.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.data.recipe.ExampleRecipe;
import com.petrolpark.core.world.item.crafting.BookRequiredCraftingRecipe;
import com.petrolpark.core.world.item.recycling.DirectRecyclingRecipe;
import com.petrolpark.core.world.item.recycling.IngredientRecyclingRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.RecipeType;

public class PetrolparkRecipeTypes {
    
    public static final RegistryEntry<RecipeType<?>, RecipeType<ExampleRecipe>> EXAMPLE = REGISTRATE.recipeType("example");
    public static final RegistryEntry<RecipeType<?>, RecipeType<DirectRecyclingRecipe>> RECYCLING = REGISTRATE.recipeType("recycling");
    public static final RegistryEntry<RecipeType<?>, RecipeType<IngredientRecyclingRecipe>> INGREDIENT_RECYCLING = REGISTRATE.recipeType("ingredient_recycling");
    public static final RegistryEntry<RecipeType<?>, RecipeType<BookRequiredCraftingRecipe>> CRAFTING_BOOK_REQUIRED = REGISTRATE.recipeType("book_required_crafting");

    public static final void register() {};
};
