package com.petrolpark.core.scratch.symbol.expression.list;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionArgument.listParameter;
import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import java.util.List;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import com.petrolpark.core.scratch.argument.IExpressionScratchParameter;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.BinaryGenericExpression;
import com.petrolpark.core.scratch.symbol.expression.ExpressionAndArguments;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.registry.scratch.PetrolparkScratchExpressionTypes;

public final class ListElementExpression<GENERIC_TYPE, GENERIC_ARGUMENT extends IScratchArgument<IScratchEnvironment, GENERIC_TYPE>> extends BinaryGenericExpression<
    IScratchEnvironment,
    GENERIC_TYPE, GENERIC_ARGUMENT,
    GENERIC_TYPE,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    List<GENERIC_TYPE>, ExpressionArgument<IScratchEnvironment, List<GENERIC_TYPE>>, ExpressionParameter<IScratchEnvironment, List<GENERIC_TYPE>>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ListElementExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> genericClass) {
        return new ListElementExpression<>(genericClass);
    };

    protected ListElementExpression(IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> genericClass) {
        super(genericClass, parameters()
            .after(listParameter("list", genericClass))
            .after(integerParameter("index"))
        );
    };

    @Override
    public GENERIC_TYPE evaluate(IScratchEnvironment environment, Long index, List<GENERIC_TYPE> list) {
        if (index < 0 || index >= list.size()) return getGenericScratchClass().fallback();
        return list.get((int)(long)index);
    };

    @Override
    public IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public GenericExpression.Type<ListElementExpression<?, ?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.LIST_ELEMENT.get();
    };

    public <ARGUMENT extends IScratchArgument<IScratchEnvironment, GENERIC_TYPE>> ARGUMENT withArguments(IExpressionScratchParameter<IScratchEnvironment, GENERIC_TYPE, ARGUMENT> parameter, ExpressionAndArguments<IScratchEnvironment, List<GENERIC_TYPE>, ?> expressionAndArguments) {
        return parameter.pass(this, ScratchArguments.arguments()
            .after(getParameters().next().get().pass(expressionAndArguments))
            .after(getParameters().get().argument(0l))
            .build()
        );
    };

    @SuppressWarnings("unchecked")
    public <TO_TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TO_TYPE>> ARGUMENT withArgumentsUnchecked(IExpressionScratchParameter<IScratchEnvironment, TO_TYPE, ARGUMENT> parameter, ExpressionAndArguments<IScratchEnvironment, List<GENERIC_TYPE>, ?> expressionAndArguments) {
        return ((ListElementExpression<TO_TYPE, ?>)this).withArguments(parameter, expressionAndArguments.uncheckedCast());
    };
    
};
