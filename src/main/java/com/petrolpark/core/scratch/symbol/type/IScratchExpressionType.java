package com.petrolpark.core.scratch.symbol.type;

import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public interface IScratchExpressionType<
    EXPRESSION extends IScratchExpression<?, ?, ?>
> extends IScratchSymbolType<EXPRESSION> {
    
};
