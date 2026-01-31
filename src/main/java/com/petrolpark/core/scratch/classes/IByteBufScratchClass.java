package com.petrolpark.core.scratch.classes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public non-sealed interface IByteBufScratchClass<TYPE> extends ISyncedScratchClass<TYPE> {
    
    @Override
    public StreamCodec<ByteBuf, TYPE> streamCodec();
};
