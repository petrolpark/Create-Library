package com.petrolpark.core.scratch.classes;

import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public interface IParseableScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends IScratchClass<TYPE, DEFAULT_ARGUMENT> {
    
    public TYPE parse(String string);
};
