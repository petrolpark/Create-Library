package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.badge.BadgeDuplicationRecipe;
import com.petrolpark.core.flags.recipe.CombineFlaggedItemsRecipe;
import com.petrolpark.core.item.decay.ageing.AgeingRecipe;
import com.petrolpark.core.item.decay.drying.DryingRecipe;
import com.petrolpark.core.item.wooden.WoodCraftingShapedRecipe;
import com.petrolpark.core.recipe.CropFertilizingRecipe;
import com.petrolpark.core.recipe.ExampleRecipe;
import com.petrolpark.core.recipe.book.RecipeBookDuplicationRecipe;
import com.petrolpark.core.recipe.crafting.BookRequiredCraftingRecipe;
import com.petrolpark.core.recipe.crafting.WrappedCraftingRecipe;
import com.petrolpark.core.recipe.recycling.DirectRecyclingRecipe;
import com.petrolpark.core.recipe.recycling.IRecyclingRecipe;
import com.petrolpark.core.recipe.recycling.IngredientRecyclingRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class PetrolparkRecipeSerializers {

    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<AgeingRecipe>> AGEING = REGISTRATE.recipeSerializer("ageing", AgeingRecipe.CODEC, AgeingRecipe.STREAM_CODEC);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<DryingRecipe>> DRYING = REGISTRATE.recipeSerializer("drying", DryingRecipe.CODEC, DryingRecipe.STREAM_CODEC);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<ExampleRecipe>> EXAMPLE = REGISTRATE.recipeSerializer("example", ExampleRecipe.CODEC, ExampleRecipe.STREAM_CODEC);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<CropFertilizingRecipe>> CROP_FERTILIZING = REGISTRATE.recipeSerializer("crop_fertilizing", CropFertilizingRecipe.CODEC, CropFertilizingRecipe.STREAM_CODEC); 
    public static final RegistryEntry<RecipeSerializer<?>, WoodCraftingShapedRecipe.Serializer> WOOD_CRAFTING_SHAPED = REGISTRATE.recipeSerializer("wood_crafting_shaped", WoodCraftingShapedRecipe.Serializer::new);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<BookRequiredCraftingRecipe>> CRAFTING_BOOK_REQUIRED = REGISTRATE.recipeSerializer("crafting_book_required", WrappedCraftingRecipe.serializer(BookRequiredCraftingRecipe::new));
    public static final RegistryEntry<RecipeSerializer<?>, IRecyclingRecipe.Serializer<DirectRecyclingRecipe>> RECYCLING = REGISTRATE.recipeSerializer("recycling", IRecyclingRecipe.serializer(DirectRecyclingRecipe::new));
    public static final RegistryEntry<RecipeSerializer<?>, IRecyclingRecipe.Serializer<IngredientRecyclingRecipe>> INGREDIENT_RECYCLING = REGISTRATE.recipeSerializer("ingredient_recycling", IRecyclingRecipe.serializer(IngredientRecyclingRecipe::new));
    //public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<ManualOnlyCraftingRecipe>> CRAFTING_MANUAL_ONLY = REGISTRATE.recipeSerializer("crafting_manual_only", WrappedCraftingRecipe.serializer(ManualOnlyCraftingRecipe::new));
    public static final RegistryEntry<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<CombineFlaggedItemsRecipe>> FLAGGED_ITEM_COMBINATION = REGISTRATE.recipeSerializer("flagged_item_combination", CombineFlaggedItemsRecipe::new);
    public static final RegistryEntry<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<BadgeDuplicationRecipe>> BADGE_DUPLICATION = REGISTRATE.recipeSerializer("badge_duplication", BadgeDuplicationRecipe::new);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<RecipeBookDuplicationRecipe>> RECIPE_BOOK_DUPLICATION = REGISTRATE.recipeSerializer("recipe_book_duplication", RecipeBookDuplicationRecipe.CODEC, RecipeBookDuplicationRecipe.STREAM_CODEC);

    public static final void register() {};
};
