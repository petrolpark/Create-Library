package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class NullaryExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    EXPRESSION extends NullaryExpressionType<ENVIRONMENT, RETURN_TYPE, ?>
> extends SimpleExpressionType<ENVIRONMENT, RETURN_TYPE, ScratchArguments.None<ENVIRONMENT>, EXPRESSION> {

    public NullaryExpressionType() {
        super(new ScratchParameters.None<>());
    };
};
