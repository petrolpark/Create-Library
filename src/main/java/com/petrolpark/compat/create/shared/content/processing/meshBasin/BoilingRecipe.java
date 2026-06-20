package com.petrolpark.compat.create.shared.content.processing.meshBasin;

import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.data.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipe;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipe.BasinBuilder;
import com.petrolpark.compat.create.shared.registry.SharedCreateRecipeTypes;
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

public class BoilingRecipe extends AdvancedBasinRecipe {
    
    public static final MapCodec<BoilingRecipe> CODEC = ProcessingRecipe.codec(BoilingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, BoilingRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(BoilingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_STREAM_CODEC);

    public static final BoilingRecipe create(ProcessingRecipeParams params) {
        if (!(params instanceof AdvancedProcessingRecipeParams advancedParams)) throw new IllegalArgumentException("Not Advanced Processing Params");
        return new BoilingRecipe(advancedParams);
    };

    protected BoilingRecipe(AdvancedProcessingRecipeParams params) {
        super(SharedCreateRecipeTypes.BOILING, params);
    };

    @Override
    public boolean isForMeshBasin() {
        return true;
    };

    public static final AdvancedProcessingRecipe.BasinBuilder<BoilingRecipe> builder(ResourceLocation id) {
        return new AdvancedProcessingRecipe.BasinBuilder<>(BoilingRecipe::create, id);
    };

    public static class Serializer implements RecipeSerializer<BoilingRecipe> {

        @Override
        public MapCodec<BoilingRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BoilingRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };

    /**
     * The base class for Deep Frying recipe generation.
     * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods to make recipes.
     */
    public static abstract class Gen extends AdvancedBasinRecipe.Gen<BoilingRecipe> {

        public Gen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
            super(output, registries, defaultNamespace);
        };

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return SharedCreateRecipeTypes.BOILING;
        };

        @Override
        protected BasinBuilder<BoilingRecipe> getBuilder(ResourceLocation id) {
            return builder(id);
        };

    };
};
