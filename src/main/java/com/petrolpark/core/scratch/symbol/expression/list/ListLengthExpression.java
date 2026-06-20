package com.petrolpark.core.scratch.symbol.expression.list;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionArgument.listParameter;

import java.util.List;

import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.classes.IntegerScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.UnaryGenericExpression;
import com.petrolpark.registry.scratch.PetrolparkScratchClasses;
import com.petrolpark.registry.scratch.PetrolparkScratchExpressionTypes;

public final class ListLengthExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends UnaryGenericExpression<
    IScratchEnvironment,
    TYPE, ARGUMENT, Long,
    List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>, ExpressionParameter<IScratchEnvironment, List<TYPE>>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ListLengthExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> scratchClass) {
        return new ListLengthExpression<>(scratchClass);
    };

    protected ListLengthExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, parameters().after(listParameter("list", genericClass)).build());
    };

    @Override
    public IntegerScratchClass getReturnClass() {
        return PetrolparkScratchClasses.INTEGER.get();
    };

    @Override
    public GenericExpression.Type<?> getExpressionType() {
        return PetrolparkScratchExpressionTypes.LIST_LENGTH.get();
    };

    @Override
    public Long evaluate(IScratchEnvironment environment, List<TYPE> list) {
        return (long)list.size();
    };
    
};
