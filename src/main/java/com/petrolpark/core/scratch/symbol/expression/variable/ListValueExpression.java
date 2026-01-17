package com.petrolpark.core.scratch.symbol.expression.variable;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;
import static com.petrolpark.core.scratch.argument.VariableArgument.variable;

import java.util.List;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public final class ListValueExpression<TYPE> extends GenericExpression<
    IVariableScratchEnvironment,
    TYPE,
    TYPE,
    ScratchArguments.And<
        IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument, ScratchArguments.And<
        IVariableScratchEnvironment, Long, ExpressionOrLiteralArgument<IVariableScratchEnvironment, Long>, ScratchArguments.Just<
        IVariableScratchEnvironment, TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>
    >>>
> {

    public static final GenericExpression.Type<ListValueExpression<?>> TYPE = new GenericExpression.Type<>(ListValueExpression::create);

    protected static final <TYPE, FALLBACK_ARGUMENT extends IScratchArgument<IVariableScratchEnvironment, TYPE>> ListValueExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new ListValueExpression<>(genericClass);
    };

    protected ListValueExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, ScratchParameters.<IVariableScratchEnvironment>parameters()
            .after(genericClass.createDefaultParameter("fallback"))
            .after(integerParameter("index"))
            .after(variable("identifier"))
            .build()
        );
    };

    @Override
    public TYPE evaluate(IVariableScratchEnvironment environment, ScratchArguments.And<IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument, ScratchArguments.And<IVariableScratchEnvironment, Long, ExpressionOrLiteralArgument<IVariableScratchEnvironment, Long>, ScratchArguments.Just<IVariableScratchEnvironment, TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>>>> arguments) {
        final List<TYPE> list = environment.getVariables(arguments.get(environment).scope()).getList(getGenericScratchClass(), arguments.get(environment).name());
        final int index = (int)(long)arguments.next().get(environment);
        if (index < 0 || index >= list.size()) return arguments.next().next().get(environment);
        return list.get(index);
    };


    @Override
    public IScratchClass<TYPE> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public IScratchExpression.Type<?> getExpressionType() {
        return TYPE;
    };
    
};
