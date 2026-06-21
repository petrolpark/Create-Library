package petrolpark.mc.library.core.scratch.symbol.block;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public non-sealed interface IInstantScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends IScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS> {
    
    public void run(ENVIRONMENT environment, ARGUMENTS arguments);
};
