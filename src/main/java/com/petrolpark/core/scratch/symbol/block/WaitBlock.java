package com.petrolpark.core.scratch.symbol.block;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.ITickingScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContext;

public class WaitBlock extends UnaryBlockType<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>, WaitBlock> {

    protected WaitBlock(IScratchParameter<ITickingScratchContext, Long, ExpressionOrLiteralArgument<ITickingScratchContext, Long>> parameter) {
        super(integerParameter("ticks"));
    };

    @Override
    public IScratchBlockInstance<ITickingScratchContext> run(ITickingScratchContext environment, IScratchContext<?> context, Long argument) {
        return new WaitBlock.Instance(argument);
    };

    @Override
    protected WaitBlock self() {
        return this;
    };

    public static class Instance implements IScratchBlockInstance<ITickingScratchContext> {
        
        protected long remainingTicks;

        protected Instance(long ticks) {
            remainingTicks = ticks;
        };

        @Override
        public boolean run(ITickingScratchContext context) {
            if (remainingTicks <= 0) return true;
            remainingTicks--;
            return false;
        };

    };
    
};
