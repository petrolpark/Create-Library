package com.petrolpark.core.scratch;

public record EvaluatedExpressionArgument<
    CONTEXT extends IScratchContext,
    TYPE,
    PARAMETERS extends ScratchParameters<? super CONTEXT>,
    ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>,
    EXPRESSION extends IScratchExpression<CONTEXT, ? super TYPE, PARAMETERS>
> (
    IScratchClass<TYPE> scratchClass,
    EXPRESSION expression,
    ARGUMENTS arguments
) 
    implements IScratchArgument<CONTEXT, TYPE> 
{

    @Override
    public TYPE get(CONTEXT context) {
        return expression().evaluate(context, arguments().asParameters(), scratchClass());
    };

    @Override
    public IScratchArgument.Type<EvaluatedExpressionArgument<?, ?, ?, ?, ?>> getType() {
        // TODO Auto-generated method stub
        return null;
    };
    
};
