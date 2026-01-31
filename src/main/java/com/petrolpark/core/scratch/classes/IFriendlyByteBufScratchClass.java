package com.petrolpark.core.scratch.classes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public non-sealed interface IFriendlyByteBufScratchClass<TYPE> extends ISyncedScratchClass<TYPE> {
  
    @Override
    public StreamCodec<FriendlyByteBuf, TYPE> streamCodec();
};
