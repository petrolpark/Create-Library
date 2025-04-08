package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.ModifiedIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.crafting.IngredientType;

public class PetrolparkIngredientTypes {

    public static final RegistryEntry<IngredientType<?>, IngredientType<ModifiedIngredient>> MODIFIED = REGISTRATE.ingredientType("modified", ModifiedIngredient.CODEC, ModifiedIngredient.STREAM_CODEC);
    
    public static final void register() {};
};
