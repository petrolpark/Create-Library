package com.petrolpark.core.scratch.symbol.expression.list;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionArgument.listParameter;

import java.util.List;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.PetrolparkScratchExpressionTypes;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.UnaryGenericExpression;

public final class ListLengthExpression<TYPE> extends UnaryGenericExpression<
    IScratchEnvironment,
    TYPE, Long,
    List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>, ExpressionParameter<IScratchEnvironment, List<TYPE>>
> {

    public static final <TYPE> ListLengthExpression<TYPE> create(IScratchClass<TYPE> scratchClass) {
        return new ListLengthExpression<>(scratchClass);
    };

    protected ListLengthExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters().after(listParameter("list", genericClass)).build());
    };

    @Override
    public IScratchClass<Long> getReturnClass() {
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
