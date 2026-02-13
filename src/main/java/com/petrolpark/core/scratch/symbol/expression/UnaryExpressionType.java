package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;;

public abstract class UnaryExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    TYPE, ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>,
    EXPRESSION extends UnaryExpressionType<ENVIRONMENT, RETURN_TYPE, TYPE, ARGUMENT, PARAMETER, EXPRESSION>
> extends SimpleExpressionType<
    ENVIRONMENT,
    RETURN_TYPE,
    ScratchArguments.Just<
        ENVIRONMENT, TYPE, ARGUMENT
    >,
    ScratchParameters.Just<
        ENVIRONMENT, TYPE, ARGUMENT, PARAMETER
    >,
    EXPRESSION
> {

    protected UnaryExpressionType(PARAMETER parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter));
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT> arguments) {
        return evaluate(environment, arguments.get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE argument);
    
};
