package com.petrolpark.core.scratch.symbol.block;

import javax.annotation.Nullable;

import com.petrolpark.core.scratch.ScratchArguments.And;
import com.petrolpark.core.scratch.ScratchArguments.Just;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;;

public abstract class BinaryBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE_1,
    ARGUMENT_1 extends IScratchArgument<ENVIRONMENT, TYPE_1>,
    TYPE_2,
    ARGUMENT_2 extends IScratchArgument<ENVIRONMENT, TYPE_2>,
    BLOCK extends BinaryBlockType<ENVIRONMENT, TYPE_1, ARGUMENT_1, TYPE_2, ARGUMENT_2, ?>
> extends SimpleBlockType<ENVIRONMENT, And<ENVIRONMENT, TYPE_1, ARGUMENT_1, Just<ENVIRONMENT, TYPE_2, ARGUMENT_2>>, BLOCK> {

    protected BinaryBlockType(IScratchParameter<ENVIRONMENT, TYPE_1, ARGUMENT_1> parameter1, IScratchParameter<ENVIRONMENT, TYPE_2, ARGUMENT_2> parameter2) {
        super(ScratchParameters.<ENVIRONMENT>parameters().after(parameter2).after(parameter1));
    };

    @Override
    @Nullable
    public final IScratchBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context, And<ENVIRONMENT, TYPE_1, ARGUMENT_1, Just<ENVIRONMENT, TYPE_2, ARGUMENT_2>> arguments) {
        return run(environment, context, arguments.get(environment, context), arguments.next().get(environment, context));
    };

    public abstract IScratchBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context, TYPE_1 argument1, TYPE_2 argument2);
    
};
