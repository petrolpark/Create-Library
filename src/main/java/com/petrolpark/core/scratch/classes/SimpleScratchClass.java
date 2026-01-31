package com.petrolpark.core.scratch.classes;

import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class SimpleScratchClass<TYPE> implements IScratchClass<TYPE>, IScratchClassType {

    protected final MapCodec<SimpleScratchClass<TYPE>> codec = MapCodec.unit(this);
    protected final StreamCodec<ByteBuf, SimpleScratchClass<TYPE>> streamCodec = StreamCodec.unit(this);

    @Override
    public final IScratchClassType getType() {
        return this;
    };

    @Override
    public final MapCodec<SimpleScratchClass<TYPE>> scratchClassCodec() {
        return codec;
    };

    @Override
    public final StreamCodec<ByteBuf, SimpleScratchClass<TYPE>> scratchClassStreamCodec() {
        return streamCodec;
    };
    
};
