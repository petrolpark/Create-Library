package com.petrolpark.shared.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.shared.SharedFeatureFlag;
import com.petrolpark.shared.world.item.crafting.CropFertilizingRecipe;
import com.petrolpark.shared.world.item.crafting.SharedRecipeType;
import com.petrolpark.shared.world.item.crafting.ageing.AgeingRecipe;
import com.petrolpark.shared.world.item.crafting.drying.DryingRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.RecipeType;

public class SharedRecipeTypes {
    
    public static final RegistryEntry<RecipeType<?>, RecipeType<AgeingRecipe>> AGEING = REGISTRATE.recipeType("ageing");
    public static final RegistryEntry<RecipeType<?>, RecipeType<CropFertilizingRecipe>> CROP_FERTILIZING = REGISTRATE.recipeType("crop_fertilizing");
    public static final RegistryEntry<RecipeType<?>, SharedRecipeType<DryingRecipe>> DRYING = REGISTRATE.sharedRecipeType(SharedFeatureFlag.DRYING_RACK, "drying");

    public static final void register() {};
};
