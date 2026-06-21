package petrolpark.mc.library.core.scratch.symbol.block;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class UnaryInstantBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE, ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>,
    BLOCK extends UnaryInstantBlockType<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, ?>
> extends SimpleInstantBlockType<ENVIRONMENT, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER>, BLOCK> {

    protected UnaryInstantBlockType(PARAMETER parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter));
    };

    @Override
    public final void run(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT> arguments) {
        run(environment, arguments.get(environment));
    };

    public abstract void run(ENVIRONMENT environment, TYPE argument);
    
};
