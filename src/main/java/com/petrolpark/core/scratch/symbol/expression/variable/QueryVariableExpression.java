package com.petrolpark.core.scratch.symbol.expression.variable;

import static com.petrolpark.core.scratch.argument.VariableArgument.variable;

import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.argument.VariableArgument.VariableParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.expression.UnaryGenericExpression;
import com.petrolpark.registry.scratch.PetrolparkScratchExpressionTypes;

public final class QueryVariableExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends UnaryGenericExpression<IVariableScratchEnvironment, TYPE, ARGUMENT, TYPE, ScratchVariableIdentifier, VariableArgument, VariableParameter> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> QueryVariableExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> genericClass) {
        return new QueryVariableExpression<>(genericClass);
    };

    protected QueryVariableExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, ScratchParameters.<IVariableScratchEnvironment>parameters().after(variable("identifier")).build());
    };

    @Override
    public TYPE evaluate(IVariableScratchEnvironment environment, ScratchVariableIdentifier argument) {
        return environment.getVariables(argument.scope()).get(getGenericScratchClass(), argument.name());
    };

    @Override
    public IScratchClass<TYPE, ARGUMENT> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public IScratchExpression.Type<?> getExpressionType() {
        return PetrolparkScratchExpressionTypes.QUERY.get();
    };
    
};
