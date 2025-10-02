package com.petrolpark.core.scratch.symbol.block;

import javax.annotation.Nullable;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchArguments.Just;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;;

public abstract class UnaryInstantiableBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE,
    ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>,
    BLOCK extends UnaryInstantiableBlockType<ENVIRONMENT, TYPE, ARGUMENT, INSTANCE, BLOCK>
> extends SimpleInstantiableBlockType<ENVIRONMENT, Just<ENVIRONMENT, TYPE, ARGUMENT>, INSTANCE, BLOCK> {

    protected UnaryInstantiableBlockType(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter));
    };

    @Override
    @Nullable
    public INSTANCE run(ENVIRONMENT environment, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT> arguments) {
        return run(environment, arguments.get(environment));
    };

    public abstract INSTANCE run(ENVIRONMENT environment, TYPE argument);
    
};
