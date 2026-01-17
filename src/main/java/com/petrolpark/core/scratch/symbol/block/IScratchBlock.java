package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public sealed interface IScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>
> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS> permits IInstantiableScratchBlock, IInstantScratchBlock {

    /**
     * Use {@link #CODEC} instead.
     */
    static ContextualCodec<IScratchEnvironment.Type<?>, IScratchBlock<?, ?>> TYPED_CODEC = ContextualCodec.dispatch(PetrolparkRegistries.SCRATCH_BLOCK_TYPES.byNameCodec(), IScratchBlock::getBlockType, IScratchBlock.Type::codec);

    public static ContextualCodec<IScratchEnvironment.Type<?>, IScratchBlock<?, ?>> CODEC = ContextualCodec.lazyInitialized(() -> TYPED_CODEC);

    public static ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, IScratchBlock<?, ?>> STREAM_CODEC = ContextualStreamCodec.dispatch(ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_BLOCK_TYPE), IScratchBlock::getBlockType, IScratchBlock.Type::streamCodec);

    public IScratchBlock.Type<?> getBlockType();

    public interface Type<BLOCK extends IScratchBlock<?, ?>> extends IScratchSymbol.Type<BLOCK> {};
};
