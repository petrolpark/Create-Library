package com.petrolpark.core.recipe.ingredient.randomizer;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkIngredientRandomizerTypes {
  
    public static final RegistryEntry<IngredientRandomizerType, IngredientRandomizerType>
    
    FROM_ARRAY = REGISTRATE.ingredientRandomizerType("from_array", FromArrayIngredientRandomizer.CODEC),
    FROM_ITEM_SET = REGISTRATE.ingredientRandomizerType("from_item_set", FromItemSetIngredientRandomizer.CODEC);

    public static final void register() {};
};
