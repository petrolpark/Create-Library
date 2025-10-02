package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class BinaryExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    TYPE_1, ARGUMENT_1 extends IScratchArgument<ENVIRONMENT, TYPE_1>,
    TYPE_2, ARGUMENT_2 extends IScratchArgument<ENVIRONMENT, TYPE_2>,
    EXPRESSION extends BinaryExpressionType<
        ENVIRONMENT,
        RETURN_TYPE,
        TYPE_1, ARGUMENT_1,
        TYPE_2, ARGUMENT_2,
        EXPRESSION
    >
> extends SimpleExpressionType<
    ENVIRONMENT,
    RETURN_TYPE,
    ScratchArguments.And<
        ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.Just<
        ENVIRONMENT, TYPE_2, ARGUMENT_2
    >>, EXPRESSION
> {

    protected BinaryExpressionType(ScratchParameters<ENVIRONMENT, ScratchArguments.And<ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.Just<ENVIRONMENT, TYPE_2, ARGUMENT_2>>> parameters) {
        super(parameters);
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.And<ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.Just<ENVIRONMENT, TYPE_2, ARGUMENT_2>> arguments) {
        return evaluate(environment, arguments.get(environment), arguments.next().get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE_1 argument1, TYPE_2 argument2);
    
};
