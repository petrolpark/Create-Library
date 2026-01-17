package com.petrolpark.core.scratch.symbol;

import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface IScratchSymbol<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> {

    public ScratchParameters<ENVIRONMENT, ARGUMENTS> getParameters();
  
    public interface Type<SYMBOL extends IScratchSymbol<?, ?>> {
        public ContextualMapCodec<IScratchEnvironment.Type<?>, SYMBOL> codec();
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, SYMBOL> streamCodec();
    };

    /**
     * Whether this Symbol can run or evaluate without crashing
     * @param arguments
     */
    public default boolean canEvaluate(ARGUMENTS arguments) {
        return true; //TODO trace through ScratchArguments
    };
};
