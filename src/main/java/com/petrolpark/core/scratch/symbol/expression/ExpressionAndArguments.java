package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public record ExpressionAndArguments<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> (IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS> expression, ARGUMENTS arguments) {
    
    public TYPE evaluate(ENVIRONMENT environment) {
        return expression().evaluate(environment, arguments());
    };
};
