package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;

public abstract class SimpleInstantiableBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>,
    BLOCK extends SimpleInstantiableBlockType<ENVIRONMENT, ARGUMENTS, INSTANCE, ?>
> extends InstantiableScratchBlock<ENVIRONMENT, ARGUMENTS, INSTANCE> implements IScratchBlock.Type<BLOCK> {

    private final ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec = ContextualMapCodec.unit(self());
    private final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec = ContextualStreamCodec.unit(self());

    protected SimpleInstantiableBlockType(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
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
