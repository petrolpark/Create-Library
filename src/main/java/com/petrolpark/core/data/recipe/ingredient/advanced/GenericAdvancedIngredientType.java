package com.petrolpark.core.data.recipe.ingredient.advanced;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class GenericAdvancedIngredientType<STACK, TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<? super STACK>> implements IAdvancedIngredientType<STACK> {

    private final MapCodec<? extends IAdvancedIngredient<? super STACK>> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, ? extends IAdvancedIngredient<? super STACK>> streamCodec;

    public GenericAdvancedIngredientType(MapCodec<TYPELESS_INGREDIENT> untypedCodec, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT> untypedStreamCodec) {
        codec = untypedCodec.xmap(untyped -> new TypeAttachedAdvancedIngredient<>(untyped, this), TypeAttachedAdvancedIngredient::untypedIngredient);
        streamCodec = untypedStreamCodec.map(untyped -> new TypeAttachedAdvancedIngredient<>(untyped, this), TypeAttachedAdvancedIngredient::untypedIngredient);
    };

    @Override
    public MapCodec<? extends IAdvancedIngredient<? super STACK>> codec() {
        return codec;
    };

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends IAdvancedIngredient<? super STACK>> streamCodec() {
        return streamCodec;
    };

    public TypeAttachedAdvancedIngredient<STACK, TYPELESS_INGREDIENT> create(TYPELESS_INGREDIENT ingredient) {
        return new TypeAttachedAdvancedIngredient<>(ingredient, this);
    };
    
};
