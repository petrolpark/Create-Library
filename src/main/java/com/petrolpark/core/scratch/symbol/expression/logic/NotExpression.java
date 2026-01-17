package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.UnaryExpressionType;

public class NotExpression extends UnaryExpressionType<
    IScratchEnvironment,
    Boolean,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>,
    NotExpression
> {
    
    protected final BooleanScratchClass scratchClass;

    protected NotExpression(BooleanScratchClass scratchClass) {
        super(scratchClass.createDefaultParameter("value"));
        this.scratchClass = scratchClass;
    };

    @Override
    public IScratchClass<Boolean> getReturnClass() {
        return scratchClass;
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, Boolean argument) {
        return !argument;
    };

    @Override
    protected NotExpression self() {
        return this;
    };
    
};
