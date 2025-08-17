package com.petrolpark.core.scratch.symbol.expression.nullary;

import com.petrolpark.core.scratch.ScratchParameterTypes;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public abstract class NullaryScratchExpression<CONTEXT extends IScratchContext, TYPE> implements IScratchExpression<CONTEXT, TYPE, ScratchParameters> {

    @Override
    public final ScratchParameterTypes<ScratchParameters> getParameterTypes() {
        return ScratchParameterTypes.NONE;
    };
};
