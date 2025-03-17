package com.petrolpark.recipe.ingredient.modifier;

import com.mojang.serialization.MapCodec;

public record IngredientModifierType(MapCodec<? extends IngredientModifier> codec) {
    
};
