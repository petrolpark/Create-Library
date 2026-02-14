package com.petrolpark.core.scratch.symbol.expression.math;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.realParameter;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import com.petrolpark.core.scratch.classes.IntegerScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.UnaryExpressionType;

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
