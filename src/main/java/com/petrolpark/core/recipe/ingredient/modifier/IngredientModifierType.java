package com.petrolpark.core.recipe.ingredient.modifier;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record IngredientModifierType<STACK>(
    String translationKey,
    MapCodec<? extends IIngredientModifier<? super STACK>> codec,
    StreamCodec<? super RegistryFriendlyByteBuf, ? extends IIngredientModifier<? super STACK>> streamCodec
) implements IIngredientModifierType<STACK> {
    
};
