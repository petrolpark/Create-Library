package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PassIngredientModifier implements IIngredientModifier<Object> {

    public static final PassIngredientModifier INSTANCE = new PassIngredientModifier();
    public static final MapCodec<PassIngredientModifier> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, PassIngredientModifier> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final IIngredientModifierType<Object> TYPE = new IngredientModifierGenericType<>("petrolpark.ingredient_modifier.pass", CODEC, STREAM_CODEC);

    @Override
    public boolean test(Object stack) {
        return true;
    };

    @Override
    public Stream<Object> modifyExamples(Stream<Object> exampleStacks) {
        return exampleStacks;
    };

    @Override
    public Stream<Object> modifyCounterExamples(Stream<Object> counterExampleStacks) {
        return Stream.empty();
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {};

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {};

    @Override
    public IIngredientModifierType<? super Object> getType() {
        return TYPE;
    };
    
};
