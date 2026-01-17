package com.petrolpark.core.scratch.symbol.expression.logic;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public final class EqualsExpression<TYPE> extends GenericExpression<
    IScratchEnvironment,
    TYPE,
    Boolean,
    ScratchArguments.And<
        IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>, ScratchArguments.Just<
        IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>
    >>
> {

    public static final GenericExpression.Type<EqualsExpression<?>> TYPE = new GenericExpression.Type<>(EqualsExpression::create);

    protected static final <TYPE> EqualsExpression<TYPE> create(IScratchClass<TYPE> scratchClass) {
        return new EqualsExpression<>(scratchClass);
    };

    protected EqualsExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("value2"))
            .after(genericClass.createDefaultParameter("value1"))
        );
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, ScratchArguments.And<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>, ScratchArguments.Just<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>>> arguments) {
        return arguments.get(environment).equals(arguments.next().get(environment));
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BOOLEAN.get();
    };

    @Override
    public IScratchExpression.Type<EqualsExpression<?>> getExpressionType() {
        return TYPE;
    };
    
};