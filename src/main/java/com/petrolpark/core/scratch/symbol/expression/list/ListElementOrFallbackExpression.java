package com.petrolpark.core.scratch.symbol.expression.list;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionArgument.listParameter;
import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import java.util.List;

import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.TernaryGenericExpression;
import com.petrolpark.registry.scratch.PetrolparkScratchExpressionTypes;

public final class ListElementOrFallbackExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends TernaryGenericExpression<
    IScratchEnvironment,
    TYPE, ARGUMENT,
    TYPE,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>, ExpressionParameter<IScratchEnvironment, List<TYPE>>,
    TYPE, ARGUMENT, IScratchParameter<IScratchEnvironment, TYPE, ARGUMENT>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ListElementOrFallbackExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> genericClass) {
        return new ListElementOrFallbackExpression<>(genericClass);
    };

    protected ListElementOrFallbackExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("fallback"))
            .after(listParameter("list", genericClass))
            .after(integerParameter("index"))
        );
    };

    @Override
    public TYPE evaluate(IScratchEnvironment environment, Long index, List<TYPE> list, TYPE fallback) {
        if (index < 0 || index >= list.size()) return fallback;
        return list.get((int)(long)index);
    };

    @Override
    public IScratchClass<TYPE, ARGUMENT> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public GenericExpression.Type<ListElementOrFallbackExpression<?, ?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.LIST_ELEMENT_OR_FALLBACK.get();
    };
    
};
