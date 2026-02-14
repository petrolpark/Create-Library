package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class TernaryGenericExpression<
    ENVIRONMENT extends IScratchEnvironment,
    GENERIC_TYPE, RETURN_TYPE,
    TYPE_1, ARGUMENT_1 extends IScratchArgument<ENVIRONMENT, TYPE_1>, PARAMETER_1 extends IScratchParameter<ENVIRONMENT, TYPE_1, ARGUMENT_1>,
    TYPE_2, ARGUMENT_2 extends IScratchArgument<ENVIRONMENT, TYPE_2>, PARAMETER_2 extends IScratchParameter<ENVIRONMENT, TYPE_2, ARGUMENT_2>,
    TYPE_3, ARGUMENT_3 extends IScratchArgument<ENVIRONMENT, TYPE_3>, PARAMETER_3 extends IScratchParameter<ENVIRONMENT, TYPE_3, ARGUMENT_3>
> extends GenericExpression<
    ENVIRONMENT,
    GENERIC_TYPE, RETURN_TYPE,
    ScratchArguments.And<
        ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.And<
        ENVIRONMENT, TYPE_2, ARGUMENT_2, ScratchArguments.Just<
        ENVIRONMENT, TYPE_3, ARGUMENT_3
    >>>,
    ScratchParameters.And<
        ENVIRONMENT, TYPE_1, ARGUMENT_1, PARAMETER_1, ScratchArguments.And<ENVIRONMENT, TYPE_2, ARGUMENT_2, ScratchArguments.Just<ENVIRONMENT, TYPE_3, ARGUMENT_3>>, ScratchParameters.And<
        ENVIRONMENT, TYPE_2, ARGUMENT_2, PARAMETER_2, ScratchArguments.Just<ENVIRONMENT, TYPE_3, ARGUMENT_3>, ScratchParameters.Just<
        ENVIRONMENT, TYPE_3, ARGUMENT_3, PARAMETER_3
    >>>
> {

    protected TernaryGenericExpression(IScratchClass<GENERIC_TYPE> genericClass, ScratchParameters.And<ENVIRONMENT, TYPE_1, ARGUMENT_1, PARAMETER_1, ScratchArguments.And<ENVIRONMENT, TYPE_2, ARGUMENT_2, ScratchArguments.Just<ENVIRONMENT, TYPE_3, ARGUMENT_3>>, ScratchParameters.And<ENVIRONMENT, TYPE_2, ARGUMENT_2, PARAMETER_2, ScratchArguments.Just<ENVIRONMENT, TYPE_3, ARGUMENT_3>, ScratchParameters.Just<ENVIRONMENT, TYPE_3, ARGUMENT_3, PARAMETER_3>>> parameters) {
        super(genericClass, parameters);
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.And<ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.And<ENVIRONMENT, TYPE_2, ARGUMENT_2, ScratchArguments.Just<ENVIRONMENT, TYPE_3, ARGUMENT_3>>> arguments) {
        return evaluate(environment, arguments.get(environment), arguments.next().get(environment), arguments.next().next().get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE_1 argument1, TYPE_2 argument2, TYPE_3 argument3);
    
};
