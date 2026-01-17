package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.ScratchSymbol;

public abstract class InstantScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS> implements IInstantScratchBlock<ENVIRONMENT, ARGUMENTS> {

    protected InstantScratchBlock(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
    };
    
};
