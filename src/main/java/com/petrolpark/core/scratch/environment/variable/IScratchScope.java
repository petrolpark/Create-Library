package com.petrolpark.core.scratch.environment.variable;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchScope {
    
    public static final Codec<IScratchScope> CODEC = PetrolparkRegistries.SCRATCH_SCOPES.byNameCodec();

    public static final StreamCodec<RegistryFriendlyByteBuf, IScratchScope> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_SCOPE);
};
