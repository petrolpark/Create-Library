package com.petrolpark.core.scratch.symbol.expression.logic;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.PetrolparkScratchExpressionTypes;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.BinaryGenericExpression;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public final class EqualsExpression<TYPE> extends BinaryGenericExpression<
    IScratchEnvironment,
    TYPE, Boolean,
    TYPE, IScratchArgument<IScratchEnvironment, TYPE>, IScratchParameter<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>>,
    TYPE, IScratchArgument<IScratchEnvironment, TYPE>, IScratchParameter<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>>
> {

    public static final <TYPE> EqualsExpression<TYPE> create(IScratchClass<TYPE> scratchClass) {
        return new EqualsExpression<>(scratchClass);
    };

    protected EqualsExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("value2"))
            .after(genericClass.createDefaultParameter("value1"))
        );
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, TYPE argument1, TYPE argument2) {
        return argument1.equals(argument2);
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BOOLEAN.get();
    };

    @Override
    public IScratchExpression.Type<EqualsExpression<?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.EQUALS.get();
    };
    
};