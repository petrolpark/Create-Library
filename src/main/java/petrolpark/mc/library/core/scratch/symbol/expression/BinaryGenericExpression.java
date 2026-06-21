package petrolpark.mc.library.core.scratch.symbol.expression;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class BinaryGenericExpression<
    ENVIRONMENT extends IScratchEnvironment,
    GENERIC_TYPE, GENERIC_ARGUMENT extends IScratchArgument<IScratchEnvironment, GENERIC_TYPE>,
    RETURN_TYPE,
    TYPE_1, ARGUMENT_1 extends IScratchArgument<? super ENVIRONMENT, TYPE_1>, PARAMETER_1 extends IScratchParameter<ENVIRONMENT, TYPE_1, ARGUMENT_1>,
    TYPE_2, ARGUMENT_2 extends IScratchArgument<? super ENVIRONMENT, TYPE_2>, PARAMETER_2 extends IScratchParameter<ENVIRONMENT, TYPE_2, ARGUMENT_2>
> extends GenericExpression<
    ENVIRONMENT,
    GENERIC_TYPE, GENERIC_ARGUMENT,
    RETURN_TYPE,
    ScratchArguments.And<
        ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.Just<
        ENVIRONMENT, TYPE_2, ARGUMENT_2
    >>,
    ScratchParameters.And<
        ENVIRONMENT, TYPE_1, ARGUMENT_1, PARAMETER_1, ScratchArguments.Just<ENVIRONMENT, TYPE_2, ARGUMENT_2>, ScratchParameters.Just<
        ENVIRONMENT, TYPE_2, ARGUMENT_2, PARAMETER_2
    >>
> {

    protected BinaryGenericExpression(IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> genericClass, ScratchParameters.And<ENVIRONMENT, TYPE_1, ARGUMENT_1, PARAMETER_1, ScratchArguments.Just<ENVIRONMENT, TYPE_2, ARGUMENT_2>, ScratchParameters.Just<ENVIRONMENT, TYPE_2, ARGUMENT_2, PARAMETER_2>> parameters) {
        super(genericClass, parameters);
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.And<ENVIRONMENT, TYPE_1, ARGUMENT_1, ScratchArguments.Just<ENVIRONMENT, TYPE_2, ARGUMENT_2>> arguments) {
        return evaluate(environment, arguments.get(environment), arguments.next().get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE_1 argument1, TYPE_2 argument2);
    
};
