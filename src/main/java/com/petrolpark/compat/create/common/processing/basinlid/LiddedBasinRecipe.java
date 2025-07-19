package com.petrolpark.compat.create.common.processing.basinlid;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.create.CreateRecipeTypes;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.biome.Biome;

public class LiddedBasinRecipe extends BasinRecipe {

    public static final MapCodec<LiddedBasinRecipe> CODEC = ProcessingRecipe.codec(LiddedBasinRecipe::new, LiddedBasinRecipe.Params.CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiddedBasinRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(LiddedBasinRecipe::new, LiddedBasinRecipe.Params.STREAM_CODEC);

    protected final Optional<HolderSet<Biome>> allowedBiomes;
    protected final Optional<ResourceLocation> firstTimeLuckyKey;
    public final boolean bubbles;

    protected static final LiddedBasinRecipe create(LiddedBasinRecipe.Params params) {
        return new LiddedBasinRecipe(params);
    };

    protected LiddedBasinRecipe(ProcessingRecipeParams params) {
        super(CreateRecipeTypes.LIDDED_BASIN, params);
        if (params instanceof LiddedBasinRecipe.Params properParams) {
            allowedBiomes = properParams.allowedBiomes();
            firstTimeLuckyKey = properParams.firstTimeLuckyKey();
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

        public static final MapCodec<ProcessingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(LiddedBasinRecipe.Params::new).forGetter(Function.identity()),
            Codec.BOOL.optionalFieldOf("bubbles", true).forGetter(LiddedBasinRecipe.Params::bubbles)
        ).apply(instance, (params, bubbles) -> {
            params.bubbles = bubbles;
            return params;
        })).flatXmap(
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
};
