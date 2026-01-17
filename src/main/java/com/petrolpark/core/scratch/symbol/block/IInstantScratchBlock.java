package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public non-sealed interface IInstantScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>
> extends IScratchBlock<ENVIRONMENT, ARGUMENTS> {
    
    public void run(ENVIRONMENT environment, ARGUMENTS arguments);
};
