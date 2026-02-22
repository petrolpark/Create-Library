package com.petrolpark.core.scratch.classes;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class SimpleScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> implements IScratchClass<TYPE, DEFAULT_ARGUMENT>, IScratchClassType {

    protected final MapCodec<SimpleScratchClass<TYPE, DEFAULT_ARGUMENT>> codec = MapCodec.unit(this);
    protected final StreamCodec<ByteBuf, SimpleScratchClass<TYPE, DEFAULT_ARGUMENT>> streamCodec = StreamCodec.unit(this);

    @Override
    public final IScratchClassType getType() {
        return this;
    };

    @Override
    public final MapCodec<SimpleScratchClass<TYPE, DEFAULT_ARGUMENT>> scratchClassCodec() {
        return codec;
    };

    @Override
    public final StreamCodec<ByteBuf, SimpleScratchClass<TYPE, DEFAULT_ARGUMENT>> scratchClassStreamCodec() {
        return streamCodec;
    };
    
};
