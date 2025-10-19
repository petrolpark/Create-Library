package com.petrolpark.core.scratch.argument;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public interface IScratchArgument<ENVIRONMENT extends IScratchEnvironment, TYPE> {
    
    public TYPE get(ENVIRONMENT environment);

    public IScratchParameter<ENVIRONMENT, TYPE, ? extends IScratchArgument<? super ENVIRONMENT, TYPE>> parameter();
};
