package com.petrolpark.core.scratch.classes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public non-sealed interface IRegistryFriendlyByteBufScratchClass<TYPE> extends ISyncedScratchClass<TYPE> {
  
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TYPE> streamCodec();
};
