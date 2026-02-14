package com.petrolpark.core.scratch.symbol.block;

import javax.annotation.Nullable;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;;

public abstract class UnaryInstantiableBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE, ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>,
    BLOCK extends UnaryInstantiableBlockType<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, INSTANCE, ?>
> extends SimpleInstantiableBlockType<ENVIRONMENT, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER>, INSTANCE, BLOCK> {

    protected UnaryInstantiableBlockType(PARAMETER parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter));
    };

    @Override
    @Nullable
    public INSTANCE run(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT> arguments) {
        return run(environment, arguments.get(environment));
    };

    public abstract INSTANCE run(ENVIRONMENT environment, TYPE argument);
    
};
