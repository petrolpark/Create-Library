package petrolpark.mc.library.core.scratch.symbol.expression.math;

import static petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.realParameter;

import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import petrolpark.mc.library.core.scratch.classes.IntegerScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.UnaryExpressionType;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;

public class RoundRealScratchExpression extends UnaryExpressionType<IScratchEnvironment, Long, Double, ExpressionOrLiteralArgument<IScratchEnvironment, Double>, ExpressionOrLiteralParameter<IScratchEnvironment, Double>, RoundRealScratchExpression> {

    public RoundRealScratchExpression() {
        super(realParameter("value"));
    };

    @Override
    public IntegerScratchClass getReturnClass() {
        return PetrolparkScratchClasses.INTEGER.get();
    };

    @Override
    public Long evaluate(IScratchEnvironment context, Double argument) {
        return Math.round(argument);
    };

    @Override
    protected RoundRealScratchExpression self() {
        return this;
    };
    
};
