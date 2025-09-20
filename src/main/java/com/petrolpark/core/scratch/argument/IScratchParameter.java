package com.petrolpark.core.scratch.argument;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface IScratchParameter<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> {

    public String key();

    public ContextualCodec<IScratchContextHolder<?>, ARGUMENT> codec();

    public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ARGUMENT> streamCodec();
};
