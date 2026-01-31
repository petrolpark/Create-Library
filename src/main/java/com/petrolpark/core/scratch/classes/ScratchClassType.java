package com.petrolpark.core.scratch.classes;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ScratchClassType<SCRATCH_CLASS extends IScratchClass<?>>(MapCodec<SCRATCH_CLASS> scratchClassCodec, StreamCodec<? super RegistryFriendlyByteBuf, SCRATCH_CLASS> scratchClassStreamCodec) implements IScratchClassType {
    
};
