package com.petrolpark.core.scratch.symbol;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.util.codec.ContextualMapCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface IScratchSymbol<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>, PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>> {

    public PARAMETERS getParameters();
  
    public interface Type<SYMBOL extends IScratchSymbol<?, ?, ?>> {
        public ContextualMapCodec<IScratchEnvironment.Type<?>, SYMBOL> codec();
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, SYMBOL> streamCodec();
    };

    /**
     * Whether this Symbol can run or evaluate without crashing
     * @param arguments
     */
    public default boolean canEvaluate(ARGUMENTS arguments) {
        return arguments.canEvaluate();
    };
};
