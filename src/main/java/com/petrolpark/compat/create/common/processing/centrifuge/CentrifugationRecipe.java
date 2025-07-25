package com.petrolpark.compat.create.common.processing.centrifuge;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.create.CreateRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public record CentrifugationRecipe(FluidIngredient input, FluidStack lightOutput, FluidStack denseOutput) implements Recipe<CentrifugationRecipe.Input> {

    public static final MapCodec<CentrifugationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidIngredient.CODEC.fieldOf("input").forGetter(CentrifugationRecipe::input),
        FluidStack.CODEC.fieldOf("light_output").forGetter(CentrifugationRecipe::lightOutput),
        FluidStack.CODEC.fieldOf("dense_output").forGetter(CentrifugationRecipe::denseOutput)
    ).apply(instance, CentrifugationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugationRecipe> STREAM_CODEC = StreamCodec.composite(
        FluidIngredient.STREAM_CODEC, CentrifugationRecipe::input,
        FluidStack.STREAM_CODEC, CentrifugationRecipe::lightOutput,
        FluidStack.STREAM_CODEC, CentrifugationRecipe::denseOutput,
        CentrifugationRecipe::new
    );

    @Override
    public boolean matches(@Nonnull Input input, @Nonnull Level level) {
        return input().test(input.input());
    };

    @Override
    public ItemStack assemble(@Nonnull Input input, @Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    };

    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    };

    @Override
    public RecipeSerializer<CentrifugationRecipe> getSerializer() {
        return CreateRecipeTypes.CENTRIFUGATION.getSerializer();
    };

    @Override
    public RecipeType<CentrifugationRecipe> getType() {
        return CreateRecipeTypes.CENTRIFUGATION.getType();
    };

    public static record Input(FluidStack input) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        };

        @Override
        public int size() {
            return 0;
        };

    };

    public static class Serializer implements RecipeSerializer<CentrifugationRecipe> {

        @Override
        public MapCodec<CentrifugationRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CentrifugationRecipe> streamCodec() {
            return STREAM_CODEC;
        };
        
    };

};
