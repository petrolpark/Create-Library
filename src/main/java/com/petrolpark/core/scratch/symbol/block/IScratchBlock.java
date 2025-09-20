package com.petrolpark.core.scratch.symbol.block;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public sealed interface IScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    BLOCK extends IScratchBlock<ENVIRONMENT, ARGUMENTS, BLOCK>
> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS> permits IInstantiableScratchBlock, IInstantScratchBlock {

    /**
     * Use {@link #CODEC} instead.
     */
    static Codec<IScratchBlock<?, ?, ?>> TYPED_CODEC = PetrolparkRegistries.SCRATCH_BLOCK_TYPES.byNameCodec().dispatch(IScratchBlock::getBlockType, IScratchBlock.Type::codec);

    public static Codec<IScratchBlock<?, ?, ?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static StreamCodec<RegistryFriendlyByteBuf, IScratchBlock<?, ?, ?>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_BLOCK_TYPE).dispatch(IScratchBlock::getBlockType, IScratchBlock.Type::streamCodec);

    public IScratchBlock.Type<BLOCK> getBlockType();

    public interface Type<BLOCK extends IScratchBlock<?, ?, ?>> extends IScratchSymbol.Type<BLOCK> {};

};
