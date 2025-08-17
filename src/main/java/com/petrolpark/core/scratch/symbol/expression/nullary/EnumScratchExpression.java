package com.petrolpark.core.scratch.symbol.expression.nullary;

import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.type.IScratchExpressionType;
import com.petrolpark.core.scratch.type.IScratchType;

public final class EnumScratchExpression<ENUM extends Enum<ENUM>> extends NullaryScratchExpression<IScratchContext, ENUM> {

    public final ENUM value;

    public EnumScratchExpression(ENUM value) {
        this.value = value;
    };

    @Override
    public ENUM evaluate(IScratchContext context, ScratchParameters arguments) {
        return value;
    };

    @Override
    public IScratchType<? super ENUM> getReturnType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReturnType'");
    };

    @Override
    public IScratchExpressionType<? extends IScratchExpression<IScratchContext, ENUM, ScratchParameters>> getSymbolType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSymbolType'");
    };
    
};
