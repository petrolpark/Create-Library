package com.petrolpark.compat.create.core.data.recipe;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class AdvancedProcessingRecipeParams extends ProcessingRecipeParams {

    public static final MapCodec<AdvancedProcessingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		codec(AdvancedProcessingRecipeParams::new).forGetter(Function.identity()),
        Codec.BOOL.optionalFieldOf("book_required", false).forGetter(AdvancedProcessingRecipeParams::isBookRequired),
		RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(AdvancedProcessingRecipeParams::allowedBiomes),
        ResourceLocation.CODEC.optionalFieldOf("first_time_lucky_key").forGetter(AdvancedProcessingRecipeParams::firstTimeLuckyKey)
	).apply(instance, (params, bookRequired, allowedBiomes, firstTimeLuckyKey) -> {
        params.bookRequired = bookRequired;
		params.allowedBiomes = allowedBiomes;
        params.firstTimeLuckyKey = firstTimeLuckyKey;
		return params;
	}));

    public static final MapCodec<ProcessingRecipeParams> UNADVANCED_CODEC = CODEC.flatXmap(DataResult::success, params -> params instanceof AdvancedProcessingRecipeParams properParams ? DataResult.success(properParams) : DataResult.error(() -> "Not Advanced Recipe Params"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedProcessingRecipeParams> STREAM_CODEC = streamCodec(AdvancedProcessingRecipeParams::new);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessingRecipeParams> UNADVANCED_STREAM_CODEC = streamCodec(AdvancedProcessingRecipeParams::new);

    protected boolean bookRequired = false;
    protected Optional<HolderSet<Biome>> allowedBiomes = Optional.empty();
    protected Optional<ResourceLocation> firstTimeLuckyKey = Optional.empty();

    public final boolean isBookRequired() {
        return bookRequired;
    };

    public final Optional<HolderSet<Biome>> allowedBiomes() {
		return allowedBiomes;
	};

    public final Optional<ResourceLocation> firstTimeLuckyKey() {
        return firstTimeLuckyKey;
    };

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.BOOL.encode(buffer, bookRequired);
        ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BIOME)).encode(buffer, allowedBiomes());
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).encode(buffer, firstTimeLuckyKey);
    };

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        bookRequired = ByteBufCodecs.BOOL.decode(buffer);
        allowedBiomes = ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BIOME)).decode(buffer);
        firstTimeLuckyKey = ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).decode(buffer);
    };
};
