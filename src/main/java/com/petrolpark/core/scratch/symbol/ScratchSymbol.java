package com.petrolpark.core.scratch.symbol;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class ScratchSymbol<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> implements IScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> {
    
    public final PARAMETERS parameters;

    protected ScratchSymbol(PARAMETERS parameters) {
        this.parameters = parameters;
    };

    @Override
    public final PARAMETERS getParameters() {
        return parameters;
    };

};
