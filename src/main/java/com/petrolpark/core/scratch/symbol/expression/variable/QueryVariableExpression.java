package com.petrolpark.core.scratch.symbol.expression.variable;

import static com.petrolpark.core.scratch.argument.VariableArgument.variable;

import com.petrolpark.PetrolparkScratchExpressionTypes;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.argument.VariableArgument.VariableParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.expression.UnaryGenericExpression;

public final class QueryVariableExpression<TYPE> extends UnaryGenericExpression<IVariableScratchEnvironment, TYPE, TYPE, ScratchVariableIdentifier, VariableArgument, VariableParameter> {

    public static final <TYPE> QueryVariableExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new QueryVariableExpression<>(genericClass);
    };

    protected QueryVariableExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, ScratchParameters.<IVariableScratchEnvironment>parameters().after(variable("identifier")).build());
    };

    @Override
    public TYPE evaluate(IVariableScratchEnvironment environment, ScratchVariableIdentifier argument) {
        return environment.getVariables(argument.scope()).get(getGenericScratchClass(), argument.name());
    };

    @Override
    public IScratchClass<TYPE> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public IScratchExpression.Type<?> getExpressionType() {
        return PetrolparkScratchExpressionTypes.QUERY.get();
    };
    
};
