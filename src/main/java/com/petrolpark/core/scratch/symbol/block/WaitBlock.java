package com.petrolpark.core.scratch.symbol.block;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.mojang.serialization.Codec;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.ITickingScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.symbol.block.IScratchBlockInstance.ScratchBlockInstanceCodecContext;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public class WaitBlock extends UnaryInstantiableBlockType<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>, WaitBlock.Instance, WaitBlock> {

    protected WaitBlock(IScratchParameter<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>> parameter) {
        super(integerParameter("ticks"));
    };

    @Override
    public WaitBlock.Instance run(ITickingScratchContext environment, IScratchContext<?> context, Long argument) {
        return new WaitBlock.Instance(argument);
    };

    @Override
    protected WaitBlock self() {
        return this;
    };

    @Override
    public ContextualCodec<ScratchBlockInstanceCodecContext<ITickingScratchContext, ScratchArguments.Just<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>>>, WaitBlock.Instance> instanceCodec() {
        return WaitBlock.Instance.CODEC;
    };

    @Override
    public ContextualStreamCodec<ByteBuf, ScratchBlockInstanceCodecContext<ITickingScratchContext, ScratchArguments.Just<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>>>, WaitBlock.Instance> instanceStreamCodec() {
        return WaitBlock.Instance.STREAM_CODEC;
    };

    public static class Instance implements IScratchBlockInstance<ITickingScratchContext> {

        public static final ContextualCodec<ScratchBlockInstanceCodecContext<ITickingScratchContext, ScratchArguments.Just<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>>>, WaitBlock.Instance> CODEC = ContextualCodec.of(Codec.LONG.xmap(WaitBlock.Instance::new, WaitBlock.Instance::remainingTicks));
        public static final ContextualStreamCodec<ByteBuf, ScratchBlockInstanceCodecContext<ITickingScratchContext, ScratchArguments.Just<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>>>, WaitBlock.Instance> STREAM_CODEC = ContextualStreamCodec.of(ByteBufCodecs.VAR_LONG.map(WaitBlock.Instance::new, WaitBlock.Instance::remainingTicks));
        
        protected long remainingTicks;

        protected Instance(long ticks) {
            remainingTicks = ticks;
        };

        public long remainingTicks() {
            return remainingTicks;
        };

        @Override
        public boolean run(ITickingScratchContext context) {
            if (remainingTicks <= 0) return true;
            remainingTicks--;
            return false;
        };

    };
    
};
