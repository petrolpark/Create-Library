package petrolpark.mc.library.core.scratch.symbol.expression.logic;

import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument.ExpressionOrDropdownParameter;
import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.UnaryExpressionType;

public final class NotExpression extends UnaryExpressionType<
    IScratchEnvironment,
    Boolean,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>, ExpressionOrDropdownParameter<IScratchEnvironment, Boolean>,
    NotExpression
> {
    
    protected final BooleanScratchClass scratchClass;

    public NotExpression(BooleanScratchClass scratchClass) {
        super(scratchClass.createDefaultParameter("value"));
        this.scratchClass = scratchClass;
    };

    @Override
    public BooleanScratchClass getReturnClass() {
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
