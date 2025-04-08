package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.recipe.ingredient.modifier.ContaminatedIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.NotIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.PassIngredientModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.codec.StreamCodec;

public class PetrolparkIngredientModifierTypes {

    public static final RegistryEntry<IngredientModifierType, IngredientModifierType>

    PASS = REGISTRATE.ingredientModifierType("pass", MapCodec.unit(PassIngredientModifier.INSTANCE), StreamCodec.unit(PassIngredientModifier.INSTANCE)),
    NOT = REGISTRATE.ingredientModifierType("not", NotIngredientModifier.CODEC, NotIngredientModifier.STREAM_CODEC),
    CONTAMINATED = REGISTRATE.ingredientModifierType("contaminated", ContaminatedIngredientModifier.CODEC, ContaminatedIngredientModifier.STREAM_CODEC);
    
    public static final void register() {};
};
