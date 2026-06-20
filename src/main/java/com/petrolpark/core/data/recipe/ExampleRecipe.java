package com.petrolpark.core.data.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.registry.PetrolparkRecipeSerializers;
import com.petrolpark.registry.PetrolparkRecipeTypes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record ExampleRecipe(Either<Ingredient, SizedFluidIngredient> ingredient, Either<ItemStack, FluidStack> result) implements IDummyRecipe {

    public static final MapCodec<ExampleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.either(Ingredient.CODEC, SizedFluidIngredient.FLAT_CODEC).fieldOf("ingredient").forGetter(ExampleRecipe::ingredient),
        Codec.either(ItemStack.CODEC, FluidStack.CODEC).fieldOf("result").forGetter(ExampleRecipe::result)
    ).apply(instance, ExampleRecipe::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, Either<Ingredient, SizedFluidIngredient>> INGREDIENT_STREAM_CODEC = ByteBufCodecs.either(Ingredient.CONTENTS_STREAM_CODEC, SizedFluidIngredient.STREAM_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Either<ItemStack, FluidStack>> RESULT_STREAM_CODEC = ByteBufCodecs.either(ItemStack.STREAM_CODEC, FluidStack.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ExampleRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.either(Ingredient.CONTENTS_STREAM_CODEC, SizedFluidIngredient.STREAM_CODEC), ExampleRecipe::ingredient,
        ByteBufCodecs.either(ItemStack.STREAM_CODEC, FluidStack.STREAM_CODEC), ExampleRecipe::result,
        ExampleRecipe::new
    );

    @Override
    public RecipeSerializer<ExampleRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.EXAMPLE.get();
    };

    @Override
    public RecipeType<ExampleRecipe> getType() {
        return PetrolparkRecipeTypes.EXAMPLE.get();
    };

};

