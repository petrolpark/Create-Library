package com.petrolpark.compat.create.common.processing.meshbasin;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.PetrolparkCreateRecipeTypes;
import com.petrolpark.compat.create.core.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
};
