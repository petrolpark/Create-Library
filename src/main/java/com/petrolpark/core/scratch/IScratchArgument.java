package com.petrolpark.core.scratch;

import com.mojang.serialization.Codec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchArgument<CONTEXT extends IScratchContext, TYPE> {
    
    public TYPE get(CONTEXT context);

    public IScratchArgument.Type<CONTEXT, TYPE, ? extends IScratchArgument<CONTEXT, TYPE>> type();

    public interface Type<CONTEXT extends IScratchContext, TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>> {

        public String key();

        public Codec<ARGUMENT> codec();

        public StreamCodec<? super RegistryFriendlyByteBuf, ARGUMENT> streamCodec();
    };
};
