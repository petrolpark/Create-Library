package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.ScratchSymbol;

public abstract class ScratchExpression<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS> implements IScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS> {

    protected ScratchExpression(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
    };
    
};
