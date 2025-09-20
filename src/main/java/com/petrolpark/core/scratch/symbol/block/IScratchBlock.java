package com.petrolpark.core.scratch.symbol.block;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    BLOCK extends IScratchBlock<ENVIRONMENT, ARGUMENTS, ?>
> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS> {

    /**
     * Use {@link #CODEC} instead.
     */
    static Codec<IScratchBlock<?, ?, ?>> TYPED_CODEC = PetrolparkRegistries.SCRATCH_BLOCK_TYPES.byNameCodec().dispatch(IScratchBlock::getBlockType, IScratchBlock.Type::codec);

    public static Codec<IScratchBlock<?, ?, ?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static StreamCodec<RegistryFriendlyByteBuf, IScratchBlock<?, ?, ?>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_BLOCK_TYPE).dispatch(IScratchBlock::getBlockType, IScratchBlock.Type::streamCodec);

    @Nullable
    public IScratchBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context, ARGUMENTS arguments);

    public IScratchBlock.Type<BLOCK> getBlockType();

    public interface Type<BLOCK extends IScratchBlock<?, ?, ?>> extends IScratchSymbol.Type<BLOCK> {};

};
