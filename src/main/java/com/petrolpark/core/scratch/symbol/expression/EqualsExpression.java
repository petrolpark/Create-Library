package com.petrolpark.core.scratch.symbol.expression;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments.And;
import com.petrolpark.core.scratch.ScratchArguments.Just;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;;

public final class EqualsExpression<TYPE> extends GenericExpression<
        IScratchEnvironment,
        TYPE,
        Boolean,
        And<
            IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>, Just<
            IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>
        >>, EqualsExpression<TYPE>
> {

    protected EqualsExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("value2"))
            .after(genericClass.createDefaultParameter("value1"))
        );
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, IScratchContext<?> context, And<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>, Just<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>>> arguments) {
        return arguments.get(environment, context).equals(arguments.next().get(environment, context));
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BOOLEAN.get();
    };

    @Override
    public IScratchExpression.Type<EqualsExpression<TYPE>> getExpressionType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExpressionType'");
    };
    
};