package com.petrolpark.core.world.item.recycling;

import java.util.Collections;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/**
 * @see DirectRecyclingRecipe
 * @see IngredientRecyclingRecipe
 */
public interface IRecyclingRecipe extends Recipe<SingleRecipeInput> {
    
    public Ingredient ingredient();

    public RecyclingOutputs outputs();

    public static <R extends IRecyclingRecipe> MapCodec<R> codec(Factory<R> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(IRecyclingRecipe::ingredient),
            RecyclingOutput.CODEC.listOf().xmap(RecyclingOutputs::new, Function.identity()).fieldOf("outputs").forGetter(IRecyclingRecipe::outputs)
        ).apply(instance, factory::create));
    };

    public static <R extends IRecyclingRecipe> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(Factory<R> factory) {
        return StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, IRecyclingRecipe::ingredient,
            RecyclingOutputs.STREAM_CODEC, IRecyclingRecipe::outputs,
            factory::create
        );
    };

    public static <R extends IRecyclingRecipe> NonNullSupplier<Serializer<R>> serializer(Factory<R> factory) {
        return () -> new Serializer<>(codec(factory), streamCodec(factory));
    };

    public static IRecyclingRecipe cast(Recipe<?> recipe) {
        if (recipe instanceof IRecyclingRecipe recyclingRecipe) return recyclingRecipe;
        return null;
    };
    
    @Override
    public default ItemStack assemble(@Nonnull SingleRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    };

    @Override
    public default boolean canCraftInDimensions(int width, int height) {
        return true;
    };

    @Override
    public default ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    };

    @Override
    public default NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(Collections.singleton(ingredient()));
    };

    public static record Serializer<R extends IRecyclingRecipe>(MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec) implements RecipeSerializer<R> {};

    @FunctionalInterface
    public static interface Factory<R extends IRecyclingRecipe> {
        public R create(Ingredient ingredient, RecyclingOutputs outputs);
    };
};
