package com.petrolpark.core.recipe.ingredient.modifier;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IIngredientModifierType<STACK> {
    
    public MapCodec<? extends IIngredientModifier<? super STACK>> codec();
    
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends IIngredientModifier<? super STACK>> streamCodec();
};
