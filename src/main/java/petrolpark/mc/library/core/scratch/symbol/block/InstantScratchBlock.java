package petrolpark.mc.library.core.scratch.symbol.block;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.ScratchSymbol;

public abstract class InstantScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IInstantScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS> {

    protected InstantScratchBlock(PARAMETERS parameters) {
        super(parameters);
    };
    
};
