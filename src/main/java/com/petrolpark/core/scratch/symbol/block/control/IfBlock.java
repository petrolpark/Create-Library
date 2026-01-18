package com.petrolpark.core.scratch.symbol.block.control;

import static com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument.booleanParameter;

import com.petrolpark.PetrolparkScratchBlockTypes;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument;
import com.petrolpark.core.scratch.argument.NestedProcedureArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;
import com.petrolpark.core.scratch.symbol.block.UnaryNestedProcedureBlock;
import com.petrolpark.core.scratch.symbol.block.instance.SimpleNestedProcedureBlockInstance;

import io.netty.buffer.ByteBuf;

public class IfBlock<ENVIRONMENT extends IScratchEnvironment> extends UnaryNestedProcedureBlock<ENVIRONMENT, Boolean, ExpressionOrDropdownArgument<ENVIRONMENT, Boolean>, SimpleNestedProcedureBlockInstance<ENVIRONMENT>, IfBlock<ENVIRONMENT>> {

    private final ContextualCodec<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Boolean, ExpressionOrDropdownArgument<ENVIRONMENT, Boolean>>>, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> instanceCodec = SimpleNestedProcedureBlockInstance.codec();
    private final ContextualStreamCodec<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Boolean, ExpressionOrDropdownArgument<ENVIRONMENT, Boolean>>>, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> instanceStreamCodec = SimpleNestedProcedureBlockInstance.streamCodec();

    public static final <ENVIRONMENT extends IScratchEnvironment> IfBlock<ENVIRONMENT> create(IScratchEnvironment.Type<ENVIRONMENT> environmentType) {
        return new IfBlock<>(environmentType);  
    };

    protected IfBlock(IScratchEnvironment.Type<ENVIRONMENT> environmentType) {
        super(booleanParameter("condition"));
    };

    @Override
    public ContextualCodec<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Boolean, ExpressionOrDropdownArgument<ENVIRONMENT, Boolean>>>, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> instanceCodec() {
        return instanceCodec;
    };

    @Override
    public ContextualStreamCodec<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Boolean, ExpressionOrDropdownArgument<ENVIRONMENT, Boolean>>>, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> instanceStreamCodec() {
        return instanceStreamCodec;
    };

    @Override
    public SimpleNestedProcedureBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, ScratchProcedure<ENVIRONMENT, SimpleNestedProcedureBlockInstance<ENVIRONMENT>> procedure, Boolean argument) {
        return argument ? new SimpleNestedProcedureBlockInstance<>(procedure) : null;
    };
    
    @Override
    public IScratchBlock.Type<IfBlock<?>> getBlockType() {
        return PetrolparkScratchBlockTypes.IF.get();
    };
};
