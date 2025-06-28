package com.petrolpark.core.recipe.ingredient.modifier;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class GenericIngredientModifierType<STACK, TYPELESS_MODIFIER extends ITypelessIngredientModifier<? super STACK>> implements IIngredientModifierType<STACK> {

    private final MapCodec<? extends IIngredientModifier<? super STACK>> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, ? extends IIngredientModifier<? super STACK>> streamCodec;

    public GenericIngredientModifierType(MapCodec<TYPELESS_MODIFIER> untypedCodec, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_MODIFIER> untypedStreamCodec) {
        codec = untypedCodec.xmap(untyped -> new TypeAttachedIngredientModifier<>(untyped, this), TypeAttachedIngredientModifier::untypedModifier);
        streamCodec = untypedStreamCodec.map(untyped -> new TypeAttachedIngredientModifier<>(untyped, this), TypeAttachedIngredientModifier::untypedModifier);
    };

    @Override
    public MapCodec<? extends IIngredientModifier<? super STACK>> codec() {
        return codec;
    };

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends IIngredientModifier<? super STACK>> streamCodec() {
        return streamCodec;
    };

    public TypeAttachedIngredientModifier<STACK, TYPELESS_MODIFIER> create(TYPELESS_MODIFIER modifier) {
        return new TypeAttachedIngredientModifier<>(modifier, this);
    };
    
};
