package com.petrolpark.compat.create.common.processing.meshbasin;

import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.PetrolparkCreateRecipeTypes;
import com.petrolpark.compat.create.core.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipe;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipe.BasinBuilder;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DeepFryingRecipe extends AdvancedBasinRecipe {
    
    public static final MapCodec<DeepFryingRecipe> CODEC = ProcessingRecipe.codec(DeepFryingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, DeepFryingRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(DeepFryingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_STREAM_CODEC);

    public static final DeepFryingRecipe create(ProcessingRecipeParams params) {
        if (!(params instanceof AdvancedProcessingRecipeParams advancedParams)) throw new IllegalArgumentException("Not Advanced Processing Params");
        return new DeepFryingRecipe(advancedParams);
    };

    protected DeepFryingRecipe(AdvancedProcessingRecipeParams params) {
        super(PetrolparkCreateRecipeTypes.DEEP_FRYING, params);
    };

    @Override
    public boolean isForMeshBasin() {
        return true;
    };

    public static final AdvancedProcessingRecipe.BasinBuilder<DeepFryingRecipe> builder(ResourceLocation id) {
        return new AdvancedProcessingRecipe.BasinBuilder<>(DeepFryingRecipe::create, id);
    };

    public static class Serializer implements RecipeSerializer<DeepFryingRecipe> {

        @Override
        public MapCodec<DeepFryingRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DeepFryingRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };

    /**
     * The base class for Deep Frying recipe generation.
     * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods to make recipes.
     */
    public static abstract class Gen extends AdvancedBasinRecipe.Gen<DeepFryingRecipe> {

        public Gen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
            super(output, registries, defaultNamespace);
        };

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return PetrolparkCreateRecipeTypes.DEEP_FRYING;
        };

        @Override
        protected BasinBuilder<DeepFryingRecipe> getBuilder(ResourceLocation id) {
            return builder(id);
        };

    };
};
