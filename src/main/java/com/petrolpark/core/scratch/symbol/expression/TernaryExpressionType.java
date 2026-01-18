package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class TernaryExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    TYPE_1, ARGUMENT_1 extends IScratchArgument<ENVIRONMENT, TYPE_1>,
    TYPE_2, ARGUMENT_2 extends IScratchArgument<ENVIRONMENT, TYPE_2>,
    TYPE_3, ARGUMENT_3 extends IScratchArgument<ENVIRONMENT, TYPE_3>,
    EXPRESSION extends TernaryExpressionType<
        ENVIRONMENT,
        RETURN_TYPE,
        TYPE_1, ARGUMENT_1,
        TYPE_2, ARGUMENT_2,
        TYPE_3, ARGUMENT_3,
        EXPRESSION
    >
> extends SimpleExpressionType<
    ENVIRONMENT,
    RETURN_TYPE,
    ScratchArguments.And<
        ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.And<
        ENVIRONMENT, TYPE_2, ARGUMENT_2, ScratchArguments.Just<
        ENVIRONMENT, TYPE_3, ARGUMENT_3
    >>>, EXPRESSION
> {

    protected TernaryExpressionType(IScratchParameter<ENVIRONMENT, TYPE_1, ARGUMENT_1> parameter1, IScratchParameter<ENVIRONMENT, TYPE_2, ARGUMENT_2> parameter2, IScratchParameter<ENVIRONMENT, TYPE_3, ARGUMENT_3> parameter3) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter3).after(parameter2).after(parameter1));
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.And<ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.And<ENVIRONMENT, TYPE_2, ARGUMENT_2, ScratchArguments.Just<ENVIRONMENT, TYPE_3, ARGUMENT_3>>> arguments) {
        return evaluate(environment, arguments.get(environment), arguments.next().get(environment), arguments.next().next().get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE_1 argument1, TYPE_2 argument2, TYPE_3 argument3);
    
};
