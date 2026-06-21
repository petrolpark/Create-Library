package petrolpark.mc.library.core.scratch.argument;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.ExpressionAndArguments;
import petrolpark.mc.library.core.scratch.symbol.expression.IScratchExpression;

public interface IExpressionScratchParameter<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> {
    
    public default <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ARGUMENT pass(IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS, ?> expression, ARGUMENTS arguments) {
        return pass(new ExpressionAndArguments<>(expression, arguments));
    };

    public <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ARGUMENT pass(ExpressionAndArguments<ENVIRONMENT, TYPE, ARGUMENTS> expressionAndArguments);
};
