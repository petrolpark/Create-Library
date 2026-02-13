package com.petrolpark.core.scratch.symbol.expression;

import static com.petrolpark.core.scratch.argument.ContextArgument.contextParameter;

import com.petrolpark.core.scratch.argument.ContextArgument;
import com.petrolpark.core.scratch.argument.ContextArgument.ContextParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;

public abstract class ContextExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    CONTEXT extends IScratchContext<CONTEXT>,
    TYPE,
    EXPRESSION extends ContextExpressionType<ENVIRONMENT, CONTEXT, TYPE, EXPRESSION>
> extends UnaryExpressionType<ENVIRONMENT, TYPE, CONTEXT, ContextArgument<ENVIRONMENT, CONTEXT>, ContextParameter<ENVIRONMENT, CONTEXT>, EXPRESSION> {

    protected ContextExpressionType() {
        super(contextParameter("context"));
    };

    @Override
    public abstract TYPE evaluate(ENVIRONMENT environment, CONTEXT context);
    
};
