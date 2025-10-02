package com.petrolpark.core.scratch.classes;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class SimpleScratchClass<TYPE> implements IScratchClass<TYPE, ExpressionArgument<IScratchEnvironment, TYPE, ?>> {

    @Override
    public IScratchParameter<IScratchEnvironment, TYPE, ExpressionArgument<IScratchEnvironment, TYPE, ?>> createDefaultParameter(String key) {
        return ExpressionArgument.parameter(key, this);
    };
    
};
