package com.petrolpark.compat.create.dough;

import com.mojang.serialization.Codec;
import com.petrolpark.RequiresCreate;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

@RequiresCreate
public class Dough {
    
    public static final Codec<Dough> CODEC = Codec.unit(new Dough());
    public static final StreamCodec<RegistryFriendlyByteBuf, Dough> STREAM_CODEC = StreamCodec.unit(new Dough());
};
