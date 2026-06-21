package petrolpark.mc.library.core.scratch.environment;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.scratch.environment.variable.IScratchScope;
import petrolpark.mc.library.core.scratch.symbol.IScratchSymbol;
import petrolpark.mc.library.registry.PetrolparkRegistries;

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
