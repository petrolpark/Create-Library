package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.ScratchSymbol;

public abstract class InstantiableScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>
> extends ScratchSymbol<ENVIRONMENT, ARGUMENTS> implements IInstantiableScratchBlock<ENVIRONMENT, ARGUMENTS, INSTANCE> {

    protected InstantiableScratchBlock(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
    };

};
