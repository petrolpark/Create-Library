package com.petrolpark.core.scratch.environment;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;

public interface IScratchEnvironment {
    
    public static interface Type<ENVIRONMENT extends IScratchEnvironment> {

        public static final Codec<IScratchEnvironment.Type<?>> CODEC = PetrolparkRegistries.SCRATCH_ENVIRONMENT_TYPES.byNameCodec();

        public default boolean allows(IScratchSymbol<? super ENVIRONMENT, ?> symbol) {
            return true;
        };

    };

    public IScratchEnvironment.Type<?> getType();
};
