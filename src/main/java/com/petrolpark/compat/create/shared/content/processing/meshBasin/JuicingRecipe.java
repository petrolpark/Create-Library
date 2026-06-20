package com.petrolpark.compat.create.shared.content.processing.meshBasin;

import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.data.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipe;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import com.petrolpark.compat.create.shared.registry.SharedCreateRecipeTypes;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class JuicingRecipe extends AdvancedBasinRecipe {

    public static final MapCodec<JuicingRecipe> CODEC = ProcessingRecipe.codec(JuicingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, JuicingRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(JuicingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_STREAM_CODEC);

    public static final JuicingRecipe create(ProcessingRecipeParams params) {
        if (!(params instanceof AdvancedProcessingRecipeParams advancedParams)) throw new IllegalArgumentException("Not Advanced Processing Params");
        return new JuicingRecipe(advancedParams);
    };

    protected JuicingRecipe(AdvancedProcessingRecipeParams params) {
        super(SharedCreateRecipeTypes.JUICING, params);
    };

    @Override
    public boolean isForMeshBasin() {
        return true;
    };

    public static final AdvancedProcessingRecipe.BasinBuilder<JuicingRecipe> builder(ResourceLocation id) {
        return new AdvancedProcessingRecipe.BasinBuilder<>(JuicingRecipe::create, id);
    };

    public static class Serializer implements RecipeSerializer<JuicingRecipe> {

        @Override
        public MapCodec<JuicingRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, JuicingRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };

    /**
     * The base class for Juicing recipe generation.
     * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods to make recipes.
     */
    public static abstract class Gen extends AdvancedBasinRecipe.Gen<JuicingRecipe> {

        public Gen(PackOutput output, CompletableFuture<Provider> registries, String defaultNamespace) {
            super(output, registries, defaultNamespace);
        };

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return SharedCreateRecipeTypes.JUICING;
        };

        @Override
        protected AdvancedProcessingRecipe.BasinBuilder<JuicingRecipe> getBuilder(ResourceLocation id) {
            return builder(id);
        };

    };
    
};
