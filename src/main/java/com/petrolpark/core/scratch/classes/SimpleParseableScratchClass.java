package com.petrolpark.core.scratch.classes;

import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class SimpleParseableScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends SimpleScratchClass<TYPE, DEFAULT_ARGUMENT> implements IParseableScratchClass<TYPE, DEFAULT_ARGUMENT> {
    
};
