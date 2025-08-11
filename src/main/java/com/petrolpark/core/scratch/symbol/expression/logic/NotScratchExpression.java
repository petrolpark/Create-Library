package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.PetrolparkScratchTypes;
import com.petrolpark.core.scratch.ScratchParameterTypes;
import com.petrolpark.core.scratch.ScratchParameterTypes.ScratchParameterTypes1;
import com.petrolpark.core.scratch.ScratchParameters.ScratchParameters1;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.type.IScratchExpressionType;

public final class NotScratchExpression extends BooleanScratchExpression<IScratchContext, ScratchParameters1<Boolean>> {

    public static final ScratchParameterTypes1<Boolean> PARAMETER_TYPES = new ScratchParameterTypes1<>(PetrolparkScratchTypes.BOOLEAN.get());

    @Override
    public Boolean evaluate(IScratchContext context, ScratchParameters1<Boolean> arguments) {
        return !arguments.get1();
    };

    @Override
    public ScratchParameterTypes<ScratchParameters1<Boolean>> getParameterTypes() {
        return PARAMETER_TYPES;
    }

    @Override
    public IScratchExpressionType<NotScratchExpression> getSymbolType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSymbolType'");
    };
    
};
