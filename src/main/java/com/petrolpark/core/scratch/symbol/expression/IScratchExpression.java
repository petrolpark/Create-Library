package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;
import com.petrolpark.core.scratch.symbol.type.IScratchExpressionType;
import com.petrolpark.core.scratch.type.IScratchType;

public interface IScratchExpression<CONTEXT extends IScratchContext, RETURN_TYPE, PARAMETERS extends ScratchParameters> extends IScratchSymbol<CONTEXT, PARAMETERS> {
    
    public RETURN_TYPE evaluate(CONTEXT context, PARAMETERS arguments);

    public IScratchType<? super RETURN_TYPE> getReturnType();

    @Override
    public IScratchExpressionType<? extends IScratchExpression<CONTEXT, RETURN_TYPE, PARAMETERS>> getSymbolType();
};
