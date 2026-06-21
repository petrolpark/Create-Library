package petrolpark.mc.library.core.scratch.symbol.expression;

import static petrolpark.mc.library.core.scratch.argument.ContextArgument.contextParameter;

import petrolpark.mc.library.core.scratch.argument.ContextArgument;
import petrolpark.mc.library.core.scratch.argument.ContextArgument.ContextParameter;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.IScratchContext;

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
