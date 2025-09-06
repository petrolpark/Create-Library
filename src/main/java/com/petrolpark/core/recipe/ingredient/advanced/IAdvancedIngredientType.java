package com.petrolpark.core.recipe.ingredient.advanced;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public interface IAdvancedIngredientType<STACK> {
    
    public MapCodec<? extends IAdvancedIngredient<? super STACK>> codec();
    
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends IAdvancedIngredient<? super STACK>> streamCodec();

    public default Stream<? extends IAdvancedIngredient<? super STACK>> streamApplicableIngredients(Level level, STACK stack) {
        return Stream.empty();
    };
};
