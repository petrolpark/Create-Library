package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;

public non-sealed interface IInstantScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    BLOCK extends IInstantScratchBlock<ENVIRONMENT, ARGUMENTS, BLOCK>
> extends IScratchBlock<ENVIRONMENT, ARGUMENTS, BLOCK> {
    
    public void run(ENVIRONMENT environment, IScratchContext<?> context, ARGUMENTS arguments);
};
