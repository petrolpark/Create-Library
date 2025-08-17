package com.petrolpark.core.scratch.type;

public record SimpleScratchType<TYPE>(Class<TYPE> clazz) implements IScratchType<TYPE> {

    @Override
    public Class<TYPE> getTypeClass() {
        return clazz;
    };
    
};
