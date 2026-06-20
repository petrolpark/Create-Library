package com.petrolpark.compat.create.shared.content.processing.basinLid;

import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.data.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import com.petrolpark.compat.create.shared.registry.SharedCreateRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class LiddedBasinRecipe extends AdvancedBasinRecipe {

    public static final MapCodec<LiddedBasinRecipe> CODEC = ProcessingRecipe.codec(LiddedBasinRecipe::new, LiddedBasinRecipe.Params.CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiddedBasinRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(LiddedBasinRecipe::new, LiddedBasinRecipe.Params.STREAM_CODEC);

    public final boolean bubbles;

    public static final LiddedBasinRecipe.Builder builder() {
        return new LiddedBasinRecipe.Builder(Petrolpark.asResource("dont_register_me"));
    };

    protected static final LiddedBasinRecipe create(LiddedBasinRecipe.Params params) {
        return new LiddedBasinRecipe(params);
    };

    protected LiddedBasinRecipe(ProcessingRecipeParams params) {
        super(SharedCreateRecipeTypes.LIDDED_BASIN, params);
        if (params instanceof LiddedBasinRecipe.Params properParams) {
            bubbles = properParams.bubbles();
        } else {
            throw new IllegalStateException("Not Lidded Basin Recipe Params");
        };
    };

    public boolean bubbles() {
        return bubbles;
    };

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (processingDuration <= 0) errors.add("Recipe does not specify a positive duration.");
        return errors;
    };

    protected static class Params extends AdvancedProcessingRecipeParams {

        public static final MapCodec<ProcessingRecipeParams> CODEC = RecordCodecBuilder.<LiddedBasinRecipe.Params>mapCodec(instance -> instance.group(
            codec(LiddedBasinRecipe.Params::new).forGetter(Function.identity()),
            Codec.BOOL.optionalFieldOf("bubbles", true).forGetter(LiddedBasinRecipe.Params::bubbles)
        ).apply(instance, (params, bubbles) -> {
            params.bubbles = bubbles;
            return params;
        })).<ProcessingRecipeParams>flatXmap(
            DataResult::success,
            params -> params instanceof LiddedBasinRecipe.Params properParams ? DataResult.success(properParams) : DataResult.error(() -> "Not Lidden Basin Recipe Params")
        );

	    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessingRecipeParams> STREAM_CODEC = streamCodec(LiddedBasinRecipe.Params::new);

        protected boolean bubbles;

        public final boolean bubbles() {
            return bubbles;
        };

        @Override
        protected void encode(RegistryFriendlyByteBuf buffer) {
            super.encode(buffer);
            ByteBufCodecs.BOOL.encode(buffer, bubbles);
        };

        @Override
        protected void decode(RegistryFriendlyByteBuf buffer) {
            super.decode(buffer);
            bubbles = ByteBufCodecs.BOOL.decode(buffer);
        };
    };
  
    public static class Serializer implements RecipeSerializer<LiddedBasinRecipe> {

		@Override
		public MapCodec<LiddedBasinRecipe> codec() {
			return CODEC;
		};

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, LiddedBasinRecipe> streamCodec() {
			return STREAM_CODEC;
		};

	};

    public static class Builder extends ProcessingRecipeBuilder<ProcessingRecipeParams, LiddedBasinRecipe, LiddedBasinRecipe.Builder> {

        public Builder(ResourceLocation recipeId) {
            super(LiddedBasinRecipe::new, recipeId);
        };

        @Override
        protected LiddedBasinRecipe.Params createParams() {
            return new LiddedBasinRecipe.Params();
        };

        @Override
        public LiddedBasinRecipe.Builder self() {
            return this;
        };

        public LiddedBasinRecipe.Builder withBubbles() {
            ((LiddedBasinRecipe.Params)params).bubbles = true;
            return self();
        };

    };
};
