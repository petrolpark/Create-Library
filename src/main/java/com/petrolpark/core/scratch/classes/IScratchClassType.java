package com.petrolpark.core.scratch.classes;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchClassType {
    
    public MapCodec<? extends IScratchClass<?, ?>> scratchClassCodec();

    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends IScratchClass<?, ?>> scratchClassStreamCodec();
};
