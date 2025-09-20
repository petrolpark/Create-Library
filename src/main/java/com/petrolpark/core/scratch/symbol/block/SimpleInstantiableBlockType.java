package com.petrolpark.core.scratch.symbol.block;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class SimpleInstantiableBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>,
    BLOCK extends SimpleInstantiableBlockType<ENVIRONMENT, ARGUMENTS, INSTANCE, BLOCK>
> extends InstantiatableScratchBlock<ENVIRONMENT, ARGUMENTS, INSTANCE, BLOCK> implements IScratchBlock.Type<BLOCK> {

    private final MapCodec<BLOCK> codec = MapCodec.unit(self());
    private final StreamCodec<ByteBuf, BLOCK> streamCodec = StreamCodec.unit(self());

    protected SimpleInstantiableBlockType(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
    };

    protected abstract BLOCK self();

    @Override
    public final MapCodec<BLOCK> codec() {
        return codec;
    };

    @Override
    public final StreamCodec<ByteBuf, BLOCK> streamCodec() {
        return streamCodec;
    };

    @Override
    public final IScratchBlock.Type<BLOCK> getBlockType() {
        return this;
    };
    
    
};
