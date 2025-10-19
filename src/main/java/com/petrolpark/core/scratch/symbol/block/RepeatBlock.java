package com.petrolpark.core.scratch.symbol.block;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.mojang.serialization.Codec;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.codec.RecordContextualCodecBuilder;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.NestedProcedureArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public class RepeatBlock<ENVIRONMENT extends IScratchEnvironment> extends UnaryNestedProcedureBlock<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>, RepeatBlock.Instance<ENVIRONMENT>, RepeatBlock<ENVIRONMENT>> {

    private final ContextualCodec<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock. Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceCodec = RecordContextualCodecBuilder.create(instance -> instance.group(
        ContextualCodec.<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock. Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, Long>of(Codec.LONG).fieldOf("repeats").forGetter(RepeatBlock.Instance::remainingRepeats),
        instance.context()
    ).apply(instance, RepeatBlock.Instance::new));

    private final ContextualStreamCodec<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceStreamCodec = ContextualStreamCodec.<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, Long>of(ByteBufCodecs.VAR_LONG).map(RepeatBlock.Instance::new, RepeatBlock.Instance::remainingRepeats);

    protected RepeatBlock(IScratchEnvironment.Type<ENVIRONMENT> contextType) {
        super(integerParameter("repeats"));
    };

    @Override
    public Instance<ENVIRONMENT> run(ENVIRONMENT environment, ScratchProcedure<ENVIRONMENT, Instance<ENVIRONMENT>> procedureArgument, Long argument) {
        return new RepeatBlock.Instance<>(procedureArgument, argument);
    };

    @Override
    public ContextualCodec<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceCodec() {
        return instanceCodec;
    };

    @Override
    public ContextualStreamCodec<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceStreamCodec() {
        return instanceStreamCodec;
    };

    @Override
    public IScratchBlock.Type<RepeatBlock<ENVIRONMENT>> getBlockType() {
        // TODO Auto-generated method stub
        return null;
    };

    public static class Instance<ENVIRONMENT extends IScratchEnvironment> extends UnaryNestedProcedureBlock.Instance<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>> {

        protected long remainingRepeats;

        protected Instance(long repeats, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>> arguments) {
            this(arguments.argument().procedure(), repeats);  
        };

        protected Instance(ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>> procedure, long repeats) {
            super(procedure);
            this.remainingRepeats = repeats;
        };

        @Override
        protected RepeatBlock.Instance<ENVIRONMENT> self() {
            return this;
        };

        public long remainingRepeats() {
            return remainingRepeats;  
        };

        @Override
        public boolean run(ENVIRONMENT environment) {
            while (remainingRepeats > 0) {
                if (procedure().run(environment)) {
                    remainingRepeats--;
                } else {
                    return false;  
                };
            };
            return true;
        };

    };
    
};
