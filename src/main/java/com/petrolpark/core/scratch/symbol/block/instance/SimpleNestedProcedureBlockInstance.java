package com.petrolpark.core.scratch.symbol.block.instance;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.NestedProcedureArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;

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
