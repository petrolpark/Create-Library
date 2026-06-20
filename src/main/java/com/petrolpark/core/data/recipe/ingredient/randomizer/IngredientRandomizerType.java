package com.petrolpark.core.data.recipe.ingredient.randomizer;

import com.mojang.serialization.MapCodec;

public record IngredientRandomizerType(MapCodec<? extends IngredientRandomizer> codec) {
    
};
