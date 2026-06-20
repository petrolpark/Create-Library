package com.petrolpark.shared.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.shared.world.item.crafting.CropFertilizingRecipe;
import com.petrolpark.shared.world.item.crafting.ageing.AgeingRecipe;
import com.petrolpark.shared.world.item.crafting.drying.DryingRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.RecipeSerializer;

public class SharedRecipeSerializers {

    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<AgeingRecipe>> AGEING = REGISTRATE.recipeSerializer("ageing", AgeingRecipe.CODEC, AgeingRecipe.STREAM_CODEC);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<DryingRecipe>> DRYING = REGISTRATE.recipeSerializer("drying", DryingRecipe.CODEC, DryingRecipe.STREAM_CODEC);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<CropFertilizingRecipe>> CROP_FERTILIZING = REGISTRATE.recipeSerializer("crop_fertilizing", CropFertilizingRecipe.CODEC, CropFertilizingRecipe.STREAM_CODEC); 

    public static final void register() {};
};
