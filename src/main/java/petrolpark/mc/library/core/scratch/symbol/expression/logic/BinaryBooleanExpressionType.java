package petrolpark.mc.library.core.scratch.symbol.expression.logic;

import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument.ExpressionOrDropdownParameter;
import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.BinaryExpressionType;

public abstract class BinaryBooleanExpressionType<EXPRESSION extends BinaryBooleanExpressionType<EXPRESSION>> extends BinaryExpressionType<
    IScratchEnvironment,
    Boolean,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>, ExpressionOrDropdownParameter<IScratchEnvironment, Boolean>,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>, ExpressionOrDropdownParameter<IScratchEnvironment, Boolean>,
    EXPRESSION
> {

    protected final BooleanScratchClass scratchClass;

    protected BinaryBooleanExpressionType(BooleanScratchClass scratchClass) {
        super(scratchClass.createDefaultParameter("argument1"), scratchClass.createDefaultParameter("argument2"));
        this.scratchClass = scratchClass;
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return scratchClass;
    };
    
};
