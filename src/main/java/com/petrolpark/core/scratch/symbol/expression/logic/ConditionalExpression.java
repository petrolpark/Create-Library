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

public final class ConditionalExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends TernaryGenericExpression<
    IScratchEnvironment,
    TYPE, ARGUMENT,
    TYPE,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>, ExpressionOrDropdownParameter<IScratchEnvironment, Boolean>,
    TYPE, ARGUMENT, IScratchParameter<IScratchEnvironment, TYPE, ARGUMENT>,
    TYPE, ARGUMENT, IScratchParameter<IScratchEnvironment, TYPE, ARGUMENT>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ConditionalExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> genericClass) {
        return new ConditionalExpression<>(genericClass);
    };

    protected ConditionalExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
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
    public IScratchClass<TYPE, ARGUMENT> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public GenericExpression.Type<ConditionalExpression<?, ?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.CONDITIONAL.get();
    };
    
};
