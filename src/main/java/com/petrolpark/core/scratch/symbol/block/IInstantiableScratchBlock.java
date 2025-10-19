package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import net.minecraft.network.RegistryFriendlyByteBuf;

public non-sealed interface IInstantiableScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>,
    BLOCK extends IInstantiableScratchBlock<ENVIRONMENT, ARGUMENTS, INSTANCE, BLOCK>
> extends IScratchBlock<ENVIRONMENT, ARGUMENTS, BLOCK> {
    
    public INSTANCE run(ENVIRONMENT environment, ARGUMENTS arguments);

    public ContextualCodec<ARGUMENTS, INSTANCE> instanceCodec();

    public ContextualStreamCodec<? super RegistryFriendlyByteBuf, ARGUMENTS, INSTANCE> instanceStreamCodec();
};
