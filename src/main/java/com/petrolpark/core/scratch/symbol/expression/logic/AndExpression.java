package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public class AndExpression extends BinaryBooleanExpressionType<AndExpression> {

    protected AndExpression(BooleanScratchClass scratchClass) {
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
