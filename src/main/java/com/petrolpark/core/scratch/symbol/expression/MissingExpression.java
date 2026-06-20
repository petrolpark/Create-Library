package com.petrolpark.core.scratch.symbol.expression;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchArguments.None;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.registry.scratch.PetrolparkScratchExpressionTypes;

public final class MissingExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends GenericExpression<IScratchEnvironment, TYPE, ARGUMENT, TYPE, ScratchArguments.None<IScratchEnvironment>, ScratchParameters.None<IScratchEnvironment>> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> MissingExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> scratchClass) {
        return new MissingExpression<>(scratchClass);
    };

    protected MissingExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, parameters());
    };

    @Override
    public TYPE evaluate(IScratchEnvironment environment, None<IScratchEnvironment> arguments) {
        throw new IllegalStateException("Tried to evalutate missing Expression");
    };

    @Override
    public boolean canEvaluate(ScratchArguments.None<IScratchEnvironment> arguments) {
        return false;
    };

    @Override
    public IScratchClass<TYPE, ARGUMENT> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public IScratchExpression.Type<?> getExpressionType() {
        return PetrolparkScratchExpressionTypes.MISSING.get();
    };
    
};
