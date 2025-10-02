package com.petrolpark.core.scratch.symbol.expression;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments.And;
import com.petrolpark.core.scratch.ScratchArguments.Just;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;;

public final class EqualsExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends GenericExpression<
        IScratchEnvironment,
        TYPE,
        Boolean,
        And<
            IScratchEnvironment, TYPE, ARGUMENT, Just<
            IScratchEnvironment, TYPE, ARGUMENT
        >>, EqualsExpression<TYPE, ARGUMENT>
> {

    protected EqualsExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("value2"))
            .after(genericClass.createDefaultParameter("value1"))
        );
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, And<IScratchEnvironment, TYPE, ARGUMENT, Just<IScratchEnvironment, TYPE, ARGUMENT>> arguments) {
        return arguments.get(environment).equals(arguments.next().get(environment));
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BOOLEAN.get();
    };

    @Override
    public IScratchExpression.Type<EqualsExpression<TYPE, ARGUMENT>> getExpressionType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExpressionType'");
    };
    
};