package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;;

public abstract class UnaryExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    TYPE_1, ARGUMENT_1 extends IScratchArgument<? super ENVIRONMENT, TYPE_1>, PARAMETER_1 extends IScratchParameter<ENVIRONMENT, TYPE_1, ARGUMENT_1>,
    EXPRESSION extends UnaryExpressionType<ENVIRONMENT, RETURN_TYPE, TYPE_1, ARGUMENT_1, PARAMETER_1, EXPRESSION>
> extends SimpleExpressionType<
    ENVIRONMENT,
    RETURN_TYPE,
    ScratchArguments.Just<
        ENVIRONMENT, TYPE_1, ARGUMENT_1
    >,
    ScratchParameters.Just<
        ENVIRONMENT, TYPE_1, ARGUMENT_1, PARAMETER_1
    >,
    EXPRESSION
> {

    protected UnaryExpressionType(PARAMETER_1 parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter));
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1> arguments) {
        return evaluate(environment, arguments.get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE_1 argument);
    
};
