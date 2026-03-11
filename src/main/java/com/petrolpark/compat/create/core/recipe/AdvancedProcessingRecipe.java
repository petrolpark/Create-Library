package com.petrolpark.compat.create.core.recipe;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.recipe.firsttimelucky.IFTLProcessingRecipe;
import com.petrolpark.core.recipe.IBiomeSpecificRecipe;
import com.petrolpark.core.recipe.INamedRecipe;
import com.petrolpark.core.recipe.book.IBookRequiredRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class AdvancedProcessingRecipe<I extends RecipeInput> extends ProcessingRecipe<I, AdvancedProcessingRecipeParams> implements IBookRequiredRecipe, IBiomeSpecificRecipe, IFTLProcessingRecipe<AdvancedProcessingRecipe<I>> {

    protected final boolean bookRequired;
    protected final Optional<HolderSet<Biome>> allowedBiomes;
    protected final Optional<ResourceLocation> firstTimeLuckyKey;

    protected Component name;

    public AdvancedProcessingRecipe(IRecipeTypeInfo typeInfo, AdvancedProcessingRecipeParams params) {
        super(typeInfo, params);
        bookRequired = params.bookRequired;
        allowedBiomes = params.allowedBiomes();
        firstTimeLuckyKey = params.firstTimeLuckyKey();
    };

    public boolean canSpecifyBookRequired() {
        return true;
    };

    @Override
    public List<String> validate() {
        final List<String> errors = super.validate();
        if (bookRequired && !canSpecifyBookRequired()) errors.add("Recipe specified that a Recipe Book is required. This type of Recipe cannot require a Recipe Book");
        return errors;
    };

    @Override
    public boolean isBookRequired(Level level) {
        return bookRequired;
    };

    @Override
    public Optional<ResourceLocation> getFirstTimeLuckyKey() {
        return firstTimeLuckyKey;
    };

    @Override
    public Optional<HolderSet<Biome>> getAllowedBiomes() {
        return allowedBiomes;
    };

    @Override
    public AdvancedProcessingRecipe<I> getAsRecipe() {
        return this;
    };

    protected void setName(Component name) {
        this.name = name;
    };

    @Override
    public Component getName(ResourceLocation recipeId) {
        return INamedRecipe.cacheDefaultName(name, this::setName, recipeId, Stream.concat(getRollableResults().stream().map(ProcessingOutput::getStack).map(ItemStack::getHoverName), getFluidResults().stream().map(FluidStack::getHoverName))::toList);
    };

    /**
     * @deprecated Use {@link IFTLProcessingRecipe#rollLuckyResults(net.minecraft.world.entity.player.Player, RandomSource)}
     */
    @Override
    @Deprecated
    public List<ItemStack> rollResults(@Nonnull RandomSource random) {
        return super.rollResults(random);
    };

    public static class Builder<R extends AdvancedProcessingRecipe<?>> extends ProcessingRecipeBuilder<AdvancedProcessingRecipeParams, R, AdvancedProcessingRecipe.Builder<R>> {

        public Builder(ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        };

        public AdvancedProcessingRecipe.Builder<R> requireRecipeBook() {
            params.bookRequired = true;
            return this;
        };

        public AdvancedProcessingRecipe.Builder<R> requireBiome(HolderSet<Biome> biomes) {
            params.allowedBiomes = Optional.of(biomes);
            return this;
        };

        public AdvancedProcessingRecipe.Builder<R> withFirstTimeLuckyKey(ResourceLocation key) {
            params.firstTimeLuckyKey = Optional.of(key);
            return this;
        };

        @Override
        protected AdvancedProcessingRecipeParams createParams() {
            return new AdvancedProcessingRecipeParams();
        };

        @Override
        public Builder<R> self() {
            return this;
        };

    };

    public static class BasinBuilder<R extends AdvancedBasinRecipe> extends ProcessingRecipeBuilder<ProcessingRecipeParams, R, AdvancedProcessingRecipe.BasinBuilder<R>> {

        public BasinBuilder(ProcessingRecipe.Factory<ProcessingRecipeParams, R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        };

        public AdvancedProcessingRecipe.BasinBuilder<R> requireRecipeBook() {
            advancedParams().bookRequired = true;
            return this;
        };

        public AdvancedProcessingRecipe.BasinBuilder<R> requireBiome(HolderSet<Biome> biomes) {
            advancedParams().allowedBiomes = Optional.of(biomes);
            return this;
        };

        public AdvancedProcessingRecipe.BasinBuilder<R> withFirstTimeLuckyKey(ResourceLocation key) {
            advancedParams().firstTimeLuckyKey = Optional.of(key);
            return this;
        };

        protected AdvancedProcessingRecipeParams advancedParams() {
            return (AdvancedProcessingRecipeParams)params;
        };

        @Override
        protected AdvancedProcessingRecipeParams createParams() {
            return new AdvancedProcessingRecipeParams();
        };

        @Override
        public AdvancedProcessingRecipe.BasinBuilder<R> self() {
            return this;
        };

    };

    public static class Serializer<R extends AdvancedProcessingRecipe<?>> implements RecipeSerializer<R> {
        
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> factory) {
			this.codec = ProcessingRecipe.codec(factory, AdvancedProcessingRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, AdvancedProcessingRecipeParams.STREAM_CODEC);
		};

		@Override
		public MapCodec<R> codec() {
			return codec;
		};

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
			return streamCodec;
		};

	};
    
};
