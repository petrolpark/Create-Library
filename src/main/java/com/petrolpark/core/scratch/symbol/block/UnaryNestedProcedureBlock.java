package com.petrolpark.core.scratch.symbol.block;

import static com.petrolpark.core.scratch.argument.NestedProcedureArgument.procedure;

import javax.annotation.Nullable;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.argument.NestedProcedureArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;
import com.petrolpark.core.scratch.symbol.block.instance.NestedProcedureBlockInstance;

public abstract class UnaryNestedProcedureBlock<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE, ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>,
    INSTANCE extends NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>,
    BLOCK extends UnaryNestedProcedureBlock<ENVIRONMENT, TYPE, ARGUMENT, INSTANCE, ?>
> extends InstantiableScratchBlock<ENVIRONMENT, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>>, INSTANCE> {
 
    private final ScratchParameters.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT>> parameters;

    protected UnaryNestedProcedureBlock(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
        this(ScratchParameters.<ENVIRONMENT>parameters()
            .after(parameter)
            .after(procedure("procedure"))
        );
    };

    private UnaryNestedProcedureBlock(ScratchParameters.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT>> parameters) {
        super(parameters);
        this.parameters = parameters;
    };

    protected ContextualCodec<IScratchContextProvider<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> procedureArgumentCodec() {
        return parameters.argumentCodec();
    };

    @Override
    @Nullable
    public final INSTANCE run(ENVIRONMENT environment, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> arguments) {
        return run(environment, arguments.get(environment), arguments.next().get(environment));
    };

    @Nullable
    public abstract INSTANCE run(ENVIRONMENT environment, ScratchProcedure<ENVIRONMENT, INSTANCE> procedure, TYPE argument);

};
