package petrolpark.mc.library.core.scratch.symbol.block;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.ScratchSymbol;

public abstract class InstantiableScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IInstantiableScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS, INSTANCE> {

    protected InstantiableScratchBlock(PARAMETERS parameters) {
        super(parameters);
    };

};
