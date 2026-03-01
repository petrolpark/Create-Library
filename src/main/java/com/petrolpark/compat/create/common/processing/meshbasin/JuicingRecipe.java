package com.petrolpark.compat.create.common.processing.meshbasin;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.CreateRecipeTypes;
import com.petrolpark.compat.create.core.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class JuicingRecipe extends AdvancedBasinRecipe {

    public static final MapCodec<JuicingRecipe> CODEC = ProcessingRecipe.codec(JuicingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, JuicingRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(JuicingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_STREAM_CODEC);

    public static final JuicingRecipe create(ProcessingRecipeParams params) {
        if (!(params instanceof AdvancedProcessingRecipeParams advancedParams)) throw new IllegalArgumentException("Not Advanced Processing Params");
        return new JuicingRecipe(advancedParams);
    };

    protected JuicingRecipe(AdvancedProcessingRecipeParams params) {
        super(CreateRecipeTypes.JUICING, params);
    };

    @Override
    public boolean isForMeshBasin() {
        return true;
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
    
};
