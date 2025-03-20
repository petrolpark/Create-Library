package com.petrolpark.core.recipe.ingredient.modifier;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkIngredientModifierTypes {

    public static final RegistryEntry<IngredientModifierType, IngredientModifierType>

    PASS = REGISTRATE.ingredientModifierType("pass", MapCodec.unit(PassIngredientModifier.INSTANCE)),
    CONTAMINATED = REGISTRATE.ingredientModifierType("contaminated", ContaminatedIngredientModifier.CODEC);
    
    public static final void register() {};
};
