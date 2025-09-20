package com.petrolpark.core.scratch.symbol.block;

import static com.petrolpark.core.scratch.argument.NestedProcedureArgument.procedure;

import com.petrolpark.core.scratch.ScratchArguments.And;
import com.petrolpark.core.scratch.ScratchArguments.Just;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.argument.NestedProcedureArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;

public abstract class UnaryNestedProcedureBlock<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE,
    ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>,
    INSTANCE extends UnaryNestedProcedureBlock.Instance<ENVIRONMENT, INSTANCE>,
    BLOCK extends UnaryNestedProcedureBlock<ENVIRONMENT, TYPE, ARGUMENT, INSTANCE, ?>
> extends ScratchBlock<ENVIRONMENT, And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, Just<ENVIRONMENT, TYPE, ARGUMENT>>, BLOCK> {

    protected UnaryNestedProcedureBlock(IScratchEnvironment.Type<ENVIRONMENT> contextType, IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
        super(ScratchParameters.<ENVIRONMENT>parameters()
            .after(parameter)
            .after(procedure("procedure"))
        );
    };

    @Override
    public final IScratchBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context, And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, Just<ENVIRONMENT, TYPE, ARGUMENT>> arguments) {
        return run(environment, context, arguments.getArgument(), arguments.next().get(environment, context));
    };

    public abstract INSTANCE run(ENVIRONMENT environment, IScratchContext<?> context, NestedProcedureArgument<ENVIRONMENT, INSTANCE> procedureArgument, TYPE argument);

    public static abstract class Instance<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends UnaryNestedProcedureBlock.Instance<ENVIRONMENT, INSTANCE>> implements NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE> {

        private final IScratchContext<?> enclosingContext;
        private final NestedProcedureArgument<ENVIRONMENT, INSTANCE> argument;

        protected Instance(IScratchContext<?> enclosingContext, NestedProcedureArgument<ENVIRONMENT, INSTANCE> argument) {
            this.enclosingContext = enclosingContext;
            this.argument = argument;
        };

        public final ScratchProcedure<ENVIRONMENT> procedure() {
            return holder().procedure();
        };

        @Override
        public final IScratchContext<?> enclosingContext() {
            return enclosingContext;
        };

        @Override
        public final NestedProcedureArgument<ENVIRONMENT, INSTANCE> holder() {
            return argument;
        };

    };

};
