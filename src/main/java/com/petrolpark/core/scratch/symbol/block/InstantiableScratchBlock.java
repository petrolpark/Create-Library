package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.ScratchSymbol;

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
