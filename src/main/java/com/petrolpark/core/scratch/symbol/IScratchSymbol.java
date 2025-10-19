package com.petrolpark.core.scratch.symbol;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchSymbol<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> {

    public ScratchParameters<ENVIRONMENT, ARGUMENTS> getParameters();
  
    public interface Type<SYMBOL extends IScratchSymbol<?, ?>> {
        public MapCodec<SYMBOL> codec();
        public StreamCodec<? super RegistryFriendlyByteBuf, SYMBOL> streamCodec();
    };
};
