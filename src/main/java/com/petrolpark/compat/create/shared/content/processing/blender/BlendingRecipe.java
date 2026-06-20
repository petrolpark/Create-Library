package com.petrolpark.compat.create.shared.content.processing.blender;

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

public class BlendingRecipe extends AdvancedBasinRecipe {
    
    public static final MapCodec<BlendingRecipe> CODEC = ProcessingRecipe.codec(BlendingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, BlendingRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(BlendingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_STREAM_CODEC);

    public static final BlendingRecipe create(ProcessingRecipeParams params) {
        if (!(params instanceof AdvancedProcessingRecipeParams advancedParams)) throw new IllegalArgumentException("Not Advanced Processing Params");
        return new BlendingRecipe(advancedParams);
    };

    protected BlendingRecipe(AdvancedProcessingRecipeParams params) {
        super(SharedCreateRecipeTypes.BLENDING, params);
    };
   
    @Override
    protected boolean canRequireHeat() {
        return false;
    };

    public static final AdvancedProcessingRecipe.BasinBuilder<BlendingRecipe> builder(ResourceLocation id) {
        return new AdvancedProcessingRecipe.BasinBuilder<>(BlendingRecipe::create, id);
    };

    public static class Serializer implements RecipeSerializer<BlendingRecipe> {

        @Override
        public MapCodec<BlendingRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlendingRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };

    /**
     * The base class for Blending recipe generation.
     * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods to make recipes.
     */
    public static abstract class Gen extends AdvancedBasinRecipe.Gen<BlendingRecipe> {

        public Gen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
            super(output, registries, defaultNamespace);
        };

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return SharedCreateRecipeTypes.BLENDING;
        };

        @Override
        protected BasinBuilder<BlendingRecipe> getBuilder(ResourceLocation id) {
            return builder(id);
        };

    };
};
