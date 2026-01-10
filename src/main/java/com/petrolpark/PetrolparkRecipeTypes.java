package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.item.decay.ageing.AgeingRecipe;
import com.petrolpark.core.item.decay.drying.DryingRecipe;
import com.petrolpark.core.recipe.CropFertilizingRecipe;
import com.petrolpark.core.recipe.crafting.BookRequiredCraftingRecipe;
import com.petrolpark.core.recipe.recycling.DirectRecyclingRecipe;
import com.petrolpark.core.recipe.recycling.IngredientRecyclingRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.RecipeType;

public class PetrolparkRecipeTypes {
    
    public static final RegistryEntry<RecipeType<?>, RecipeType<AgeingRecipe>> AGEING = REGISTRATE.recipeType("ageing");
    public static final RegistryEntry<RecipeType<?>, RecipeType<DryingRecipe>> DRYING = REGISTRATE.recipeType("drying");
    public static final RegistryEntry<RecipeType<?>, RecipeType<CropFertilizingRecipe>> CROP_FERTILIZING = REGISTRATE.recipeType("crop_fertilizing");
    public static final RegistryEntry<RecipeType<?>, RecipeType<DirectRecyclingRecipe>> RECYCLING = REGISTRATE.recipeType("recycling");
    public static final RegistryEntry<RecipeType<?>, RecipeType<IngredientRecyclingRecipe>> INGREDIENT_RECYCLING = REGISTRATE.recipeType("ingredient_recycling");
    public static final RegistryEntry<RecipeType<?>, RecipeType<BookRequiredCraftingRecipe>> CRAFTING_BOOK_REQUIRED = REGISTRATE.recipeType("book_required_crafting");

    public static final void register() {};
};
