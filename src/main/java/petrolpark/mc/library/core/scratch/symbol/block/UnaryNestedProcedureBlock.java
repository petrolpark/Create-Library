package petrolpark.mc.library.core.scratch.symbol.block;

import static petrolpark.mc.library.core.scratch.argument.NestedProcedureArgument.procedure;

import javax.annotation.Nullable;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.argument.NestedProcedureArgument;
import petrolpark.mc.library.core.scratch.argument.NestedProcedureArgument.NestedProcedureParameter;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.IScratchContextProvider;
import petrolpark.mc.library.core.scratch.procedure.ScratchProcedure;
import petrolpark.mc.library.core.scratch.symbol.block.instance.NestedProcedureBlockInstance;
import petrolpark.mc.library.util.codec.ContextualCodec;

public abstract class UnaryNestedProcedureBlock<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE, ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>,
    INSTANCE extends NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>,
    BLOCK extends UnaryNestedProcedureBlock<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, INSTANCE, ?>
> extends InstantiableScratchBlock<
    ENVIRONMENT,
    ScratchArguments.And<
        ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<
        ENVIRONMENT, TYPE, ARGUMENT
    >>,
    ScratchParameters.And<
        ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, NestedProcedureParameter<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<
        ENVIRONMENT, TYPE, ARGUMENT, PARAMETER
    >>,
    INSTANCE
> {

    protected UnaryNestedProcedureBlock(PARAMETER parameter) {
        this(ScratchParameters.<ENVIRONMENT>parameters()
            .after(parameter)
            .after(procedure("procedure"))
        );
    };

    private UnaryNestedProcedureBlock(ScratchParameters.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>, NestedProcedureParameter<ENVIRONMENT, INSTANCE>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER>> parameters) {
        super(parameters);
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
