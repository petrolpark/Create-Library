package com.petrolpark.compat.create.core.dough;

import com.mojang.serialization.Codec;
import com.petrolpark.RequiresCreate;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

@RequiresCreate
public class DoughType {
    
    public static final Codec<DoughType> CODEC = Codec.unit(new DoughType());
    public static final StreamCodec<RegistryFriendlyByteBuf, DoughType> STREAM_CODEC = StreamCodec.unit(new DoughType());
};
