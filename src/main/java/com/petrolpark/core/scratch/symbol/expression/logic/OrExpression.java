package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public class OrExpression extends BinaryBooleanExpressionType<OrExpression> {

    protected OrExpression(BooleanScratchClass scratchClass) {
        super(scratchClass);
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, Boolean argument1, Boolean argument2) {
        return argument1 || argument2;
    };

    @Override
    protected OrExpression self() {
        return this;
    };
    
};
