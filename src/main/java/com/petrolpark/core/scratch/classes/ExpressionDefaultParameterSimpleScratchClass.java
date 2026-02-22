package com.petrolpark.core.scratch.classes;

import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class ExpressionDefaultParameterSimpleScratchClass<TYPE> extends SimpleScratchClass<TYPE, ExpressionArgument<IScratchEnvironment, TYPE>> {
    
    @Override
    public ExpressionParameter<IScratchEnvironment, TYPE> createDefaultParameter(String key) {
        return ExpressionArgument.parameter(key, this);
    };
};
