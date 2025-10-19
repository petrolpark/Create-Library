package com.petrolpark.core.scratch.symbol;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class ScratchSymbol<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> implements IScratchSymbol<ENVIRONMENT, ARGUMENTS> {
    
    public final ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters;

    protected ScratchSymbol(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        this.parameters = parameters;
    };

    @Override
    public final ScratchParameters<ENVIRONMENT, ARGUMENTS> getParameters() {
        return parameters;
    };

};
