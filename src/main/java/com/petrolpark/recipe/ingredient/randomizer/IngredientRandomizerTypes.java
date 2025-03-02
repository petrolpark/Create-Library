package com.petrolpark.recipe.ingredient.randomizer;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

public class IngredientRandomizerTypes {
  
    public static final RegistryEntry<IngredientRandomizerType> FROM_ARRAY = REGISTRATE.get().ingredientRandomizerType("from_array", new FromArrayIngredientRandomizer.Serializer());
    public static final RegistryEntry<IngredientRandomizerType> OUT_OF_TAG = REGISTRATE.get().ingredientRandomizerType("out_of_tag", new OutOfTagIngredientRandomizer.Serializer());

    public static final void register() {};
};
