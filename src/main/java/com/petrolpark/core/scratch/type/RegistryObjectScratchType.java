package com.petrolpark.core.scratch.type;

import net.minecraft.core.Registry;

public record RegistryObjectScratchType<TYPE>(Class<TYPE> objectClass, Registry<TYPE> registry) implements IScratchType<TYPE> {

    @Override
    public Class<TYPE> getTypeClass() {
        return objectClass;
    };
    
};
