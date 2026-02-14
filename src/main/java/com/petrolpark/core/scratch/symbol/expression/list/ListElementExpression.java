package com.petrolpark.core.scratch.symbol.expression.list;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionArgument.listParameter;
import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import java.util.List;

import com.petrolpark.PetrolparkScratchExpressionTypes;
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

public final class ListElementExpression<TYPE> extends BinaryGenericExpression<
    IScratchEnvironment,
    TYPE, TYPE,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>, ExpressionParameter<IScratchEnvironment, List<TYPE>>
> {

    public static final <TYPE> ListElementExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new ListElementExpression<>(genericClass);
    };

    protected ListElementExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters()
            .after(listParameter("list", genericClass))
            .after(integerParameter("index"))
        );
    };

    @Override
    public TYPE evaluate(IScratchEnvironment environment, Long index, List<TYPE> list) {
        if (index < 0 || index >= list.size()) return getGenericScratchClass().fallback();
        return list.get((int)(long)index);
    };

    @Override
    public IScratchClass<TYPE> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public GenericExpression.Type<ListElementExpression<?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.LIST_ELEMENT.get();
    };

    public <ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ARGUMENT withArguments(IExpressionScratchParameter<IScratchEnvironment, TYPE, ARGUMENT> parameter, ExpressionAndArguments<IScratchEnvironment, List<TYPE>, ?> expressionAndArguments) {
        return parameter.pass(this, ScratchArguments.arguments()
            .after(getParameters().next().get().pass(expressionAndArguments))
            .after(getParameters().get().argument(0l))
            .build()
        );
    };

    @SuppressWarnings("unchecked")
    public <TO_TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TO_TYPE>> ARGUMENT withArgumentsUnchecked(IExpressionScratchParameter<IScratchEnvironment, TO_TYPE, ARGUMENT> parameter, ExpressionAndArguments<IScratchEnvironment, List<TYPE>, ?> expressionAndArguments) {
        return ((ListElementExpression<TO_TYPE>)this).withArguments(parameter, expressionAndArguments.uncheckedCast());
    };
    
};
