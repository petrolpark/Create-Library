package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.ScratchSymbol;

public abstract class ScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    BLOCK extends ScratchBlock<ENVIRONMENT, ARGUMENTS, ?>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS> implements IScratchBlock<ENVIRONMENT, ARGUMENTS, BLOCK> {

    protected ScratchBlock(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
    };

};
