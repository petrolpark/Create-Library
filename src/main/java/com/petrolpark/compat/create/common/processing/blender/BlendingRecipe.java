package com.petrolpark.compat.create.common.processing.blender;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.PetrolparkCreateRecipeTypes;
import com.petrolpark.compat.create.core.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class BlendingRecipe extends AdvancedBasinRecipe {
    
    public static final MapCodec<BlendingRecipe> CODEC = ProcessingRecipe.codec(BlendingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, BlendingRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(BlendingRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_STREAM_CODEC);

    public static final BlendingRecipe create(ProcessingRecipeParams params) {
        if (!(params instanceof AdvancedProcessingRecipeParams advancedParams)) throw new IllegalArgumentException("Not Advanced Processing Params");
        return new BlendingRecipe(advancedParams);
    };

    protected BlendingRecipe(AdvancedProcessingRecipeParams params) {
        super(PetrolparkCreateRecipeTypes.BLENDING, params);
    };
   
    @Override
    protected boolean canRequireHeat() {
        return false;
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
};
