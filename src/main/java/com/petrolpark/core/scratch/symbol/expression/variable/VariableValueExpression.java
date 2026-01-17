package com.petrolpark.core.scratch.symbol.expression.variable;

import static com.petrolpark.core.scratch.argument.VariableArgument.variable;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public final class VariableValueExpression<TYPE> extends GenericExpression<IVariableScratchEnvironment, TYPE, TYPE, ScratchArguments.Just<IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument>> {

    public static final GenericExpression.Type<VariableValueExpression<?>> TYPE = new GenericExpression.Type<>(VariableValueExpression::create);

    protected static final <TYPE> VariableValueExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new VariableValueExpression<>(genericClass);
    };

    protected VariableValueExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, ScratchParameters.<IVariableScratchEnvironment>parameters().after(variable("identifier")).build());
    };

    @Override
    public TYPE evaluate(IVariableScratchEnvironment environment, ScratchArguments.Just<IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument> arguments) {
        return environment.getVariables(arguments.get(environment).scope()).get(getGenericScratchClass(), arguments.get(environment).name());
    };

    @Override
    public IScratchClass<TYPE> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public IScratchExpression.Type<?> getExpressionType() {
        return TYPE;
    };
    
};
