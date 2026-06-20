package com.petrolpark.core.scratch.environment;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.environment.variable.IScratchScope;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;
import com.petrolpark.registry.PetrolparkRegistries;

public interface IScratchEnvironment {
    
    public static interface Type<ENVIRONMENT extends IScratchEnvironment> {

        public static final Codec<IScratchEnvironment.Type<?>> CODEC = PetrolparkRegistries.SCRATCH_ENVIRONMENT_TYPES.byNameCodec();

        public default boolean allows(IScratchSymbol<? super ENVIRONMENT, ?, ?> symbol) {
            return true;
        };

        public boolean canAccess(IScratchScope scope);

    };

    public IScratchEnvironment.Type<?> getType();
};
