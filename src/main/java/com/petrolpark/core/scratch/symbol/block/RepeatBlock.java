package com.petrolpark.core.scratch.symbol.block;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.NestedProcedureArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;

public class RepeatBlock<ENVIRONMENT extends IScratchEnvironment> extends UnaryNestedProcedureBlock<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>, RepeatBlock.Instance<ENVIRONMENT>, RepeatBlock<ENVIRONMENT>> {

    protected RepeatBlock(IScratchEnvironment.Type<ENVIRONMENT> contextType) {
        super(contextType, integerParameter("repeats"));
    };

    @Override
    public Instance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context, NestedProcedureArgument<ENVIRONMENT, Instance<ENVIRONMENT>> procedureArgument, Long argument) {
        return new RepeatBlock.Instance<>(context, procedureArgument, argument);
    };

    @Override
    public IScratchBlock.Type<RepeatBlock<ENVIRONMENT>> getBlockType() {
        // TODO Auto-generated method stub
        return null;
    };

    public static class Instance<ENVIRONMENT extends IScratchEnvironment> extends UnaryNestedProcedureBlock.Instance<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>> {

        protected long remainingRepeats;

        protected Instance(IScratchContext<?> enclosingContext, NestedProcedureArgument<ENVIRONMENT, Instance<ENVIRONMENT>> argument, long repeats) {
            super(enclosingContext, argument);
        };

        @Override
        public boolean run(ENVIRONMENT environment) {
            while (remainingRepeats > 0) {
                if (procedure().run(environment, this)) {
                    remainingRepeats--;
                } else {
                    return false;  
                };
            };
            return true;
        };

    };
    
};
