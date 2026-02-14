package com.petrolpark.core.scratch.symbol.block.control;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.mojang.serialization.Codec;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import com.petrolpark.core.scratch.environment.ITickingEnvironment;
import com.petrolpark.core.scratch.symbol.block.IScratchBlockInstance;
import com.petrolpark.core.scratch.symbol.block.UnaryInstantiableBlockType;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public class WaitBlock extends UnaryInstantiableBlockType<ITickingEnvironment, Long, ExpressionOrLiteralArgument<ITickingEnvironment, Long>, ExpressionOrLiteralParameter<ITickingEnvironment, Long>, WaitBlock.Instance, WaitBlock> {

    public WaitBlock() {
        super(integerParameter("ticks"));
    };

    @Override
    public WaitBlock.Instance run(ITickingEnvironment environment, Long argument) {
        return new WaitBlock.Instance(argument);
    };

    @Override
    protected WaitBlock self() {
        return this;
    };

    @Override
    public ContextualCodec<ScratchArguments.Just<ITickingEnvironment, Long, ExpressionOrLiteralArgument<ITickingEnvironment, Long>>, WaitBlock.Instance> instanceCodec() {
        return WaitBlock.Instance.CODEC;
    };

    @Override
    public ContextualStreamCodec<ByteBuf, ScratchArguments.Just<ITickingEnvironment, Long, ExpressionOrLiteralArgument<ITickingEnvironment, Long>>, WaitBlock.Instance> instanceStreamCodec() {
        return WaitBlock.Instance.STREAM_CODEC;
    };

    public static class Instance implements IScratchBlockInstance<ITickingEnvironment> {

        public static final ContextualCodec<ScratchArguments.Just<ITickingEnvironment, Long, ExpressionOrLiteralArgument<ITickingEnvironment, Long>>, WaitBlock.Instance> CODEC = ContextualCodec.of(Codec.LONG.xmap(WaitBlock.Instance::new, WaitBlock.Instance::remainingTicks));
        public static final ContextualStreamCodec<ByteBuf, ScratchArguments.Just<ITickingEnvironment, Long, ExpressionOrLiteralArgument<ITickingEnvironment, Long>>, WaitBlock.Instance> STREAM_CODEC = ContextualStreamCodec.of(ByteBufCodecs.VAR_LONG.map(WaitBlock.Instance::new, WaitBlock.Instance::remainingTicks));
        
        protected long remainingTicks;

        protected Instance(long ticks) {
            remainingTicks = ticks;
        };

        public long remainingTicks() {
            return remainingTicks;
        };

        @Override
        public boolean tick(ITickingEnvironment context) {
            if (remainingTicks <= 0) return true;
            remainingTicks--;
            return false;
        };

    };
    
};
