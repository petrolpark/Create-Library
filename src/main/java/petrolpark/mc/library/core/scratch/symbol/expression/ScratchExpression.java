package petrolpark.mc.library.core.scratch.symbol.expression;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.ScratchSymbol;

public abstract class ScratchExpression<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, PARAMETERS> {

    protected ScratchExpression(PARAMETERS parameters) {
        super(parameters);
    };
    
};
