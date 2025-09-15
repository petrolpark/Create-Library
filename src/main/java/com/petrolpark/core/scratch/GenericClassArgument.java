package com.petrolpark.core.scratch;

public record GenericClassArgument(IScratchClass<?> scratchClass) implements IScratchArgument<IScratchContext, IScratchClass<?>> {

    @Override
    public IScratchClass<?> get(IScratchContext context) {
        return scratchClass();
    };

    @Override
    public IScratchArgument.Type<GenericClassArgument> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
