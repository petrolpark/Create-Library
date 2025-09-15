package com.petrolpark.core.scratch;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchSymbol<CONTEXT extends IScratchContext, PARAMETERS extends ScratchParameters<CONTEXT>, ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>> {

    public ScratchSignature<CONTEXT, PARAMETERS, ARGUMENTS> getSignature();
  
    public interface Type<SYMBOL extends IScratchSymbol<?, ?, ?>> {
        public MapCodec<SYMBOL> codec();
        public StreamCodec<? super RegistryFriendlyByteBuf, SYMBOL> streamCodec();
    };
};
