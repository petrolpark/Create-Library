package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.BinaryExpressionType;

public abstract class BinaryBooleanExpressionType<EXPRESSION extends BinaryBooleanExpressionType<EXPRESSION>> extends BinaryExpressionType<
    IScratchEnvironment,
    Boolean,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>,
    EXPRESSION
> {

    protected final BooleanScratchClass scratchClass;

    protected BinaryBooleanExpressionType(BooleanScratchClass scratchClass) {
        super(scratchClass.createDefaultParameter("argument1"), scratchClass.createDefaultParameter("argument2"));
        this.scratchClass = scratchClass;
    };

    @Override
    public IScratchClass<Boolean> getReturnClass() {
        return scratchClass;
    };
    
};
