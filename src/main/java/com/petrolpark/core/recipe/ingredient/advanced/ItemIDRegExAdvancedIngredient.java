package com.petrolpark.core.recipe.ingredient.advanced;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkAdvancedIngredientTypes;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ItemIDRegExAdvancedIngredient(String idRegEx) implements ItemAdvancedIngredient {

    public static final MapCodec<ItemIDRegExAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(Codec.STRING, "regex", ItemIDRegExAdvancedIngredient::idRegEx, ItemIDRegExAdvancedIngredient::new);
    public static final StreamCodec<ByteBuf, ItemIDRegExAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ItemIDRegExAdvancedIngredient::idRegEx, ItemIDRegExAdvancedIngredient::new);

    @Override
    public Stream<? extends ItemStack> streamExamples() {
        return BuiltInRegistries.ITEM.stream().filter(this::test).map(ItemStack::new); //TODO see if caching this is better
    };

    @Override
    public Stream<? extends ItemStack> streamCounterExamples() {
        return BuiltInRegistries.ITEM.stream().dropWhile(this::test).map(ItemStack::new);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(translate(idRegEx()));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(translateInverse(idRegEx()));
    };

    @Override
    public boolean test(ItemStack stack) {
        return test(stack.getItem());
    };

    public boolean test(Item item) {
        return Pattern.matches(idRegEx, BuiltInRegistries.ITEM.getKey(item).getPath());
    };

    @Override
    public INamedAdvancedIngredientType<ItemStack> getType() {
        return PetrolparkAdvancedIngredientTypes.ITEM_ID_REGEX.get();
    };
    
};
