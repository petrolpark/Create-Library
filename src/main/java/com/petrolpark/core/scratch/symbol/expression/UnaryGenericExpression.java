package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class UnaryGenericExpression<
    ENVIRONMENT extends IScratchEnvironment,
    GENERIC_TYPE, RETURN_TYPE,
    TYPE_1, ARGUMENT_1 extends IScratchArgument<ENVIRONMENT, TYPE_1>
> extends GenericExpression<
    ENVIRONMENT,
    GENERIC_TYPE, RETURN_TYPE,
    ScratchArguments.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1>
> {

    protected UnaryGenericExpression(IScratchClass<GENERIC_TYPE> genericClass, ScratchParameters<ENVIRONMENT, ScratchArguments.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1>> parameters) {
        super(genericClass, parameters);
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1> arguments) {
        return evaluate(environment, arguments.get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE_1 argument);
    
};
