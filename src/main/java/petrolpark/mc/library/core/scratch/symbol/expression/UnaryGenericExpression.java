package petrolpark.mc.library.core.scratch.symbol.expression;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class UnaryGenericExpression<
    ENVIRONMENT extends IScratchEnvironment,
    GENERIC_TYPE, GENERIC_ARGUMENT extends IScratchArgument<IScratchEnvironment, GENERIC_TYPE>, 
    RETURN_TYPE,
    TYPE_1, ARGUMENT_1 extends IScratchArgument<? super ENVIRONMENT, TYPE_1>, PARAMETER_1 extends IScratchParameter<ENVIRONMENT, TYPE_1, ARGUMENT_1>
> extends GenericExpression<
    ENVIRONMENT,
    GENERIC_TYPE, GENERIC_ARGUMENT,
    RETURN_TYPE,
    ScratchArguments.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1>,
    ScratchParameters.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1, PARAMETER_1>
> {

    protected UnaryGenericExpression(IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> genericClass, ScratchParameters.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1, PARAMETER_1> parameters) {
        super(genericClass, parameters);
    };

    @Override
    public final RETURN_TYPE evaluate(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE_1, ARGUMENT_1> arguments) {
        return evaluate(environment, arguments.get(environment));
    };

    public abstract RETURN_TYPE evaluate(ENVIRONMENT environment, TYPE_1 argument);
    
};
