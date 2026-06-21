package petrolpark.mc.library.core.scratch.symbol.expression;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class NullaryExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    EXPRESSION extends NullaryExpressionType<ENVIRONMENT, RETURN_TYPE, EXPRESSION>
> extends SimpleExpressionType<ENVIRONMENT, RETURN_TYPE, ScratchArguments.None<ENVIRONMENT>, ScratchParameters.None<ENVIRONMENT>, EXPRESSION> {

    public NullaryExpressionType() {
        super(new ScratchParameters.None<>());
    };
};
