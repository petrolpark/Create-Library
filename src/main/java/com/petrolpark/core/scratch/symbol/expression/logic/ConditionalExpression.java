package com.petrolpark.core.scratch.symbol.expression.logic;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument.booleanParameter;

import com.petrolpark.PetrolparkScratchExpressionTypes;
import com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument.ExpressionOrDropdownParameter;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.TernaryGenericExpression;;

public final class ConditionalExpression<TYPE> extends TernaryGenericExpression<
    IScratchEnvironment,
    TYPE, TYPE,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>, ExpressionOrDropdownParameter<IScratchEnvironment, Boolean>,
    TYPE, IScratchArgument<IScratchEnvironment, TYPE>, IScratchParameter<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>>,
    TYPE, IScratchArgument<IScratchEnvironment, TYPE>, IScratchParameter<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>>
> {

    public static final <TYPE> ConditionalExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new ConditionalExpression<>(genericClass);
    };

    protected ConditionalExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("fail"))
            .after(genericClass.createDefaultParameter("pass"))
            .after(booleanParameter("condition"))
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
    public GenericExpression.Type<ConditionalExpression<?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.CONDITIONAL.get();
    };
    
};
