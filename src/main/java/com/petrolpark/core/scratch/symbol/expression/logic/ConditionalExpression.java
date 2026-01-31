package com.petrolpark.core.scratch.symbol.expression.logic;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionArgument.parameter;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.expression.TernaryGenericExpression;;

public final class ConditionalExpression<TYPE> extends TernaryGenericExpression<
    IScratchEnvironment,
    TYPE, TYPE,
    Boolean, ExpressionArgument<IScratchEnvironment, Boolean>,
    TYPE, IScratchArgument<IScratchEnvironment, TYPE>,
    TYPE, IScratchArgument<IScratchEnvironment, TYPE>
> {

    public static final <TYPE> ConditionalExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new ConditionalExpression<>(genericClass);
    };

    protected ConditionalExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("fail"))
            .after(genericClass.createDefaultParameter("pass"))
            .after(parameter("condition", PetrolparkScratchClasses.BOOLEAN.get()))
        );
    };

    @Override
    public TYPE evaluate(IScratchEnvironment environment, Boolean argument1, TYPE argument2, TYPE argument3) {
        if (argument1) {
            return argument2;
        } else {
            return argument3;
        }
    };

    @Override
    public IScratchClass<TYPE> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public IScratchExpression.Type<ConditionalExpression<TYPE>> getExpressionType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExpressionType'");
    };
    
};
