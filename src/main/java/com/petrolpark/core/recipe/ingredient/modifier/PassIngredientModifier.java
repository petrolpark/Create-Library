package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
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
    public void modifyExamples(List<? extends Object> exampleStacks) {};

    @Override
    public void modifyCounterExamples(List<? extends Object> counterExampleStacks) {
        counterExampleStacks.clear();
    };

    @Override
    public void addToDescription(List<Component> description) {};

    @Override
    public void addToCounterDescription(List<Component> description) {};

    @Override
    public IIngredientModifierType<? super Object> getType() {
        return TYPE;
    };
    
};
