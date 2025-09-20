package com.petrolpark.core.scratch.symbol.block;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class SimpleBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    BLOCK extends SimpleBlockType<ENVIRONMENT, ARGUMENTS, ?>
> extends ScratchBlock<ENVIRONMENT, ARGUMENTS, BLOCK> implements IScratchBlock.Type<BLOCK> {

    private final MapCodec<BLOCK> codec = MapCodec.unit(self());
    private final StreamCodec<ByteBuf, BLOCK> streamCodec = StreamCodec.unit(self());

    protected SimpleBlockType(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
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
