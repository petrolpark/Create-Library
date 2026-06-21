package petrolpark.mc.library.core.scratch.classes;

import org.jetbrains.annotations.ApiStatus;

import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public sealed interface ISyncedScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends IScratchClass<TYPE, DEFAULT_ARGUMENT> permits IByteBufScratchClass, IFriendlyByteBufScratchClass, IRegistryFriendlyByteBufScratchClass {
  
    @Override
    @ApiStatus.NonExtendable
    public default ISyncedScratchClass<TYPE, DEFAULT_ARGUMENT> asSynced() {
        return this;
    };
};
