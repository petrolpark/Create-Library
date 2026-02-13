package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.ScratchSymbol;

public abstract class ScratchExpression<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, PARAMETERS> {

    protected ScratchExpression(PARAMETERS parameters) {
        super(parameters);
    };
    
};
