package petrolpark.mc.library.core.scratch.symbol.expression.logic;

import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public class XorExpression extends BinaryBooleanExpressionType<XorExpression> {

    public XorExpression(BooleanScratchClass scratchClass) {
        super(scratchClass);
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, Boolean argument1, Boolean argument2) {
        return argument1 ^ argument2;
    };

    @Override
    protected XorExpression self() {
        return this;
    };
    
};
