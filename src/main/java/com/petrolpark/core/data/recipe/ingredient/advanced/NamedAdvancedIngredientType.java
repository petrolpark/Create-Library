package com.petrolpark.core.data.recipe.ingredient.advanced;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NamedAdvancedIngredientType<STACK>(
    String translationKey,
    MapCodec<? extends IAdvancedIngredient<? super STACK>> codec,
    StreamCodec<? super RegistryFriendlyByteBuf, ? extends IAdvancedIngredient<? super STACK>> streamCodec
) implements INamedAdvancedIngredientType<STACK> {
    
};
