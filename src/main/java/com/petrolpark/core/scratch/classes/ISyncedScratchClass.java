package com.petrolpark.core.scratch.classes;

import org.jetbrains.annotations.ApiStatus;

public sealed interface ISyncedScratchClass<TYPE> extends IScratchClass<TYPE> permits IByteBufScratchClass, IFriendlyByteBufScratchClass, IRegistryFriendlyByteBufScratchClass {
  
    @Override
    @ApiStatus.NonExtendable
    public default ISyncedScratchClass<TYPE> asSynced() {
        return this;
    };
};
