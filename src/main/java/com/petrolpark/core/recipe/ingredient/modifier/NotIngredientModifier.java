package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.util.CodecHelper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public record NotIngredientModifier<STACK>(IIngredientModifier<? super STACK> modifier) implements ITypelessIngredientModifier<STACK> {

    public static final <STACK> MapCodec<NotIngredientModifier<STACK>> codec(Codec<IIngredientModifier<? super STACK>> typeCodec) {
        return CodecHelper.singleFieldMap(typeCodec, "modifier", NotIngredientModifier::modifier, NotIngredientModifier::new);
    };

    public static final <STACK> StreamCodec<? super RegistryFriendlyByteBuf, NotIngredientModifier<STACK>> streamCodec(StreamCodec<? super RegistryFriendlyByteBuf, IIngredientModifier<? super STACK>> typeStreamCodec) {
        return StreamCodec.composite(typeStreamCodec, NotIngredientModifier::modifier, NotIngredientModifier::new);
    };
    
    @Override
    public boolean test(STACK stack) {
        return !modifier().test(stack);
    };

    @Override
    public void modifyExamples(List<? extends STACK> exampleStacks) {
        modifier().modifyCounterExamples(exampleStacks);
    };

    @Override
    public void modifyCounterExamples(List<? extends STACK> counterExampleStacks) {
        modifier().modifyExamples(counterExampleStacks);
    };

    @Override
    public void addToDescription(List<Component> description) {
        modifier().addToCounterDescription(description);
    };

    @Override
    public void addToCounterDescription(List<Component> description) {
        modifier().addToDescription(description);
    };
    
};
