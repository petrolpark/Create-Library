package petrolpark.mc.library.core.scratch.symbol.expression.variable;

import static petrolpark.mc.library.core.scratch.argument.VariableArgument.variable;

import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.VariableArgument;
import petrolpark.mc.library.core.scratch.argument.VariableArgument.VariableParameter;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.environment.variable.IVariableScratchEnvironment;
import petrolpark.mc.library.core.scratch.environment.variable.ScratchVariableIdentifier;
import petrolpark.mc.library.core.scratch.symbol.expression.IScratchExpression;
import petrolpark.mc.library.core.scratch.symbol.expression.UnaryGenericExpression;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchExpressionTypes;

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
