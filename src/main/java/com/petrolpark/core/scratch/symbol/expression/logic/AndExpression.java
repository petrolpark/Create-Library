package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public final class AndExpression extends BinaryBooleanExpressionType<AndExpression> {

    public AndExpression(BooleanScratchClass scratchClass) {
        super(scratchClass);
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, Boolean argument1, Boolean argument2) {
        return argument1 && argument2;
    };

    @Override
    protected AndExpression self() {
        return this;
    };
    
};
