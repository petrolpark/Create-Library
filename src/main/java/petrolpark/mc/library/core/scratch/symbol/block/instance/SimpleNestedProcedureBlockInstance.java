package petrolpark.mc.library.core.scratch.symbol.block.instance;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.argument.NestedProcedureArgument;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.ScratchProcedure;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import io.netty.buffer.ByteBuf;

public class SimpleNestedProcedureBlockInstance<ENVIRONMENT extends IScratchEnvironment> extends NestedProcedureBlockInstance<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> {

    public static final <ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>>> ContextualCodec<ARGUMENTS, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> codec() {
        return ContextualCodec.of(SimpleNestedProcedureBlockInstance::new);
    };

    public static final <ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>>> ContextualStreamCodec<ByteBuf, ARGUMENTS, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> streamCodec() {
        return ContextualStreamCodec.of(SimpleNestedProcedureBlockInstance::new);
    };

    public SimpleNestedProcedureBlockInstance(ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> procedure) {
        super(procedure);
    };

    protected SimpleNestedProcedureBlockInstance(ScratchArguments.More<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>> arguments) {
        this(arguments.argument().procedure());
    };

    @Override
    public boolean tick(ENVIRONMENT environment) {
        return procedure().tick(environment);
    };

    @Override
    protected SimpleNestedProcedureBlockInstance<ENVIRONMENT> self() {
        return this;
    };
    
};
