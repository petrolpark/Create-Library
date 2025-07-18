package com.petrolpark.compat.create.core.recipe;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.recipe.firsttimelucky.IFTLProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public abstract class AdvancedProcessingRecipe extends ProcessingRecipe<RecipeWrapper, AdvancedProcessingRecipeParams> implements IFTLProcessingRecipe<AdvancedProcessingRecipe>, IBiomeSpecificProcessingRecipe {

    protected final Optional<HolderSet<Biome>> allowedBiomes;
    protected final Optional<ResourceLocation> firstTimeLuckyKey;

    public AdvancedProcessingRecipe(IRecipeTypeInfo typeInfo, AdvancedProcessingRecipeParams params) {
        super(typeInfo, params);
        allowedBiomes = params.allowedBiomes();
        firstTimeLuckyKey = params.firstTimeLuckyKey();
    };

    @Override
    public Optional<HolderSet<Biome>> getAllowedBiomes() {
        return allowedBiomes;
    };

    @Override
    public AdvancedProcessingRecipe getAsRecipe() {
        return this;
    };

    /**
     * @deprecated Use {@link IFTLProcessingRecipe#rollLuckyResults(com.simibubi.create.foundation.blockEntity.SmartBlockEntity)}
     */
    @Override
    @Deprecated
    public List<ItemStack> rollResults() {
        return super.rollResults();
    };

    /**
     * @deprecated Use {@link IFTLProcessingRecipe#rollLuckyResults(com.simibubi.create.foundation.blockEntity.SmartBlockEntity)}
     */
    @Override
    @Deprecated
    public List<ItemStack> rollResults(@Nonnull List<ProcessingOutput> rollableResults) {
        return super.rollResults(rollableResults);
    };

    public static class Serializer<R extends AdvancedProcessingRecipe> implements RecipeSerializer<R> {
        
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
