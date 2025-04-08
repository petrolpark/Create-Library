package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkIngredientModifierTypes;
import com.petrolpark.util.CodecHelper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record NotIngredientModifier(IngredientModifier modifier) implements IngredientModifier {

    public static final MapCodec<NotIngredientModifier> CODEC = CodecHelper.singleFieldMap(IngredientModifier.CODEC, "modifier", NotIngredientModifier::modifier, NotIngredientModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, NotIngredientModifier> STREAM_CODEC = StreamCodec.composite(IngredientModifier.STREAM_CODEC, NotIngredientModifier::modifier, NotIngredientModifier::new);

    @Override
    public boolean test(ItemStack stack) {
        return !modifier().test(stack);
    };

    @Override
    public void modifyExamples(List<ItemStack> exampleStacks) {
        modifier().modifyCounterExamples(exampleStacks);
    };

    @Override
    public void modifyCounterExamples(List<ItemStack> counterExampleStacks) {
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

    @Override
    public IngredientModifierType getType() {
        return PetrolparkIngredientModifierTypes.NOT.get();
    };
    
};
