package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public interface IIngredientModifierType<STACK> {
    
    public MapCodec<? extends IIngredientModifier<? super STACK>> codec();
    
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends IIngredientModifier<? super STACK>> streamCodec();

    public default Stream<? extends IIngredientModifier<? super STACK>> streamApplicableModifiers(Level level, STACK stack) {
        return Stream.empty();
    };
};
