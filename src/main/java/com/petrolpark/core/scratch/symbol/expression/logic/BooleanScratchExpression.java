package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.PetrolparkScratchTypes;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.type.IScratchType;

public abstract class BooleanScratchExpression<CONTEXT extends IScratchContext, PARAMETERS extends ScratchParameters> implements IScratchExpression<CONTEXT, Boolean, PARAMETERS> {
    
    @Override
    public IScratchType<Boolean> getReturnType() {
        return PetrolparkScratchTypes.BOOLEAN.get();
    };
};
