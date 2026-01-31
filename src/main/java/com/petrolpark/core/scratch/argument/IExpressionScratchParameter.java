package com.petrolpark.core.scratch.argument;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.ExpressionAndArguments;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public interface IExpressionScratchParameter<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> {
    
    public default <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ARGUMENT argument(IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS> expression, ARGUMENTS arguments) {
        return argument(new ExpressionAndArguments<>(expression, arguments));
    };

    public <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ARGUMENT argument(ExpressionAndArguments<ENVIRONMENT, TYPE, ARGUMENTS> expressionAndArguments);
};
