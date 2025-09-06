package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.randomizer.FromArrayIngredientRandomizer;
import com.petrolpark.core.recipe.ingredient.randomizer.FromItemSetIngredientRandomizer;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkIngredientRandomizerTypes {
  
    public static final RegistryEntry<IngredientRandomizerType, IngredientRandomizerType>
    
    FROM_ARRAY = REGISTRATE.ingredientRandomizerType("from_array", FromArrayIngredientRandomizer.CODEC),
    FROM_ITEM_SET = REGISTRATE.ingredientRandomizerType("from_item_set", FromItemSetIngredientRandomizer.CODEC);

    public static final void register() {};
};
