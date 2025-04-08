package com.petrolpark.core.recipe.ingredient.modifier;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record IngredientModifierType(String translationKey, MapCodec<? extends IngredientModifier> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IngredientModifier> streamCodec) {
    
};
