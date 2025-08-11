package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.ScratchParameters.ScratchParameters2;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.type.IScratchExpressionType;

public final class AndScratchExpression extends BooleanBinaryOperatorScratchExpression {

    @Override
    public Boolean evaluate(IScratchContext context, ScratchParameters2<Boolean, Boolean> arguments) {
        return arguments.get1() && arguments.get2();
    }

    @Override
    public IScratchExpressionType<AndScratchExpression> getSymbolType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSymbolType'");
    };


    
};
