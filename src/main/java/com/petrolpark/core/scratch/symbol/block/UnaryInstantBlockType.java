package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public abstract class UnaryInstantBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE, ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>,
    BLOCK extends UnaryInstantBlockType<ENVIRONMENT, TYPE, ARGUMENT, ?>
> extends SimpleInstantBlockType<ENVIRONMENT, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, BLOCK> {

    protected UnaryInstantBlockType(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter));
    };

    @Override
    public final void run(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT> arguments) {
        run(environment, arguments.get(environment));
    };

    public abstract void run(ENVIRONMENT environment, TYPE argument);
    
};
