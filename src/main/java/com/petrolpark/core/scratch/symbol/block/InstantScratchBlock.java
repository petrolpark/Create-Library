package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.ScratchSymbol;

public abstract class InstantScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IInstantScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS> {

    protected InstantScratchBlock(PARAMETERS parameters) {
        super(parameters);
    };
    
};
