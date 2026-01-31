package com.petrolpark.core.scratch.symbol.expression.list;

import java.util.List;

import com.petrolpark.PetrolparkScratchExpressionTypes;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
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
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>,
    List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>
> {

    public static final <TYPE> ListElementExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new ListElementExpression<>(genericClass);
    };

    protected final ScratchParameters.And<IScratchEnvironment, Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>, ScratchArguments.Just<IScratchEnvironment, List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>>, ScratchParameters.Just<IScratchEnvironment, List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>, ExpressionParameter<IScratchEnvironment, List<TYPE>>>> parameters;

    protected ListElementExpression(IScratchClass<TYPE> genericClass) {
        this(genericClass, ScratchParameters.<IScratchEnvironment>parameters()
            .after(ExpressionArgument.listParameter("list", genericClass))
            .after(ExpressionOrLiteralArgument.integerParameter("index"))
        );
    };

    protected ListElementExpression(IScratchClass<TYPE> genericClass, ScratchParameters.And<IScratchEnvironment, Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>, ScratchArguments.Just<IScratchEnvironment, List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>>, ScratchParameters.Just<IScratchEnvironment, List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>, ExpressionParameter<IScratchEnvironment, List<TYPE>>>> parameters) {
        super(genericClass, parameters);
        this.parameters = parameters;
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
        return parameter.argument(this, ScratchArguments.arguments()
            .after(parameters.next().get().argument(expressionAndArguments))
            .after(parameters.get().argument(0l))
            .build()
        );
    };
    
};
