package com.petrolpark.core.scratch.symbol.block;

import static com.petrolpark.core.scratch.argument.NestedProcedureArgument.procedure;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.argument.NestedProcedureArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;

public abstract class UnaryNestedProcedureBlock<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE,
    ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>,
    INSTANCE extends UnaryNestedProcedureBlock.Instance<ENVIRONMENT, INSTANCE>,
    BLOCK extends UnaryNestedProcedureBlock<ENVIRONMENT, TYPE, ARGUMENT, INSTANCE, BLOCK>
> extends InstantiatableScratchBlock<ENVIRONMENT, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>>, INSTANCE, BLOCK> {

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

    protected ContextualCodec<IScratchContextHolder<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> procedureArgumentCodec() {
        return parameters.argumentCodec();
    };

    @Override
    public final INSTANCE run(ENVIRONMENT environment, IScratchContext<?> context, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> arguments) {
        return run(environment, context, arguments.get(environment, context), arguments.next().get(environment, context));
    };

    public abstract INSTANCE run(ENVIRONMENT environment, IScratchContext<?> context, ScratchProcedure<ENVIRONMENT, INSTANCE> procedure, TYPE argument);

    public static abstract class Instance<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends UnaryNestedProcedureBlock.Instance<ENVIRONMENT, INSTANCE>> implements NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE> {

        private final IScratchContext<?> enclosingContext;
        private final ScratchProcedure<ENVIRONMENT, INSTANCE> procedure;

        protected Instance(IScratchContext<?> enclosingContext, ScratchProcedure<ENVIRONMENT, INSTANCE> procedure) {
            this.enclosingContext = enclosingContext;
            this.procedure = procedure;
        };

        public final ScratchProcedure<ENVIRONMENT, INSTANCE> procedure() {
            return procedure;
        };

        @Override
        public final IScratchContext<?> enclosingContext() {
            return enclosingContext;
        };

        @Override
        public final ScratchProcedure<ENVIRONMENT, INSTANCE> holder() {
            return procedure();
        };

    };

};
