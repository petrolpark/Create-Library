package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.util.codec.ContextualMapCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;

import io.netty.buffer.ByteBuf;

public abstract class SimpleInstantBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>,
    BLOCK extends SimpleInstantBlockType<ENVIRONMENT, ARGUMENTS, PARAMETERS, ?>
> extends InstantScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IScratchBlock.Type<BLOCK> {

    private final ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec = ContextualMapCodec.unit(self());
    private final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec = ContextualStreamCodec.unit(self());
    
    protected SimpleInstantBlockType(PARAMETERS parameters) {
        super(parameters);
    };

    protected abstract BLOCK self();

    @Override
    public final ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec() {
        return codec;
    };

    @Override
    public final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec() {
        return streamCodec;
    };

    @Override
    public final IScratchBlock.Type<BLOCK> getBlockType() {
        return this;
    };
};
