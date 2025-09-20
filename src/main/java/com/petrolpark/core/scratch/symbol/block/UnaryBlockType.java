package com.petrolpark.core.scratch.symbol.block;

import javax.annotation.Nullable;

import com.petrolpark.core.scratch.ScratchArguments.Just;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;;

public abstract class UnaryBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE,
    ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>,
    BLOCK extends UnaryBlockType<ENVIRONMENT, TYPE, ARGUMENT, ?>
> extends SimpleBlockType<ENVIRONMENT, Just<ENVIRONMENT, TYPE, ARGUMENT>, BLOCK> {

    protected UnaryBlockType(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter));
    };

    @Override
    @Nullable
    public IScratchBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context, Just<ENVIRONMENT, TYPE, ARGUMENT> arguments) {
        return run(environment, context, arguments.get(environment, context));
    };

    public abstract IScratchBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context, TYPE argument);
    
};
