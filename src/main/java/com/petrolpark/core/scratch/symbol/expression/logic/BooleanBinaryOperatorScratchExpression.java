package com.petrolpark.core.scratch.symbol.expression.logic;

import com.petrolpark.core.scratch.PetrolparkScratchTypes;
import com.petrolpark.core.scratch.ScratchParameterTypes;
import com.petrolpark.core.scratch.ScratchParameterTypes.ScratchParameterTypes2;
import com.petrolpark.core.scratch.ScratchParameters.ScratchParameters2;
import com.petrolpark.core.scratch.context.IScratchContext;

public abstract class BooleanBinaryOperatorScratchExpression extends BooleanScratchExpression<IScratchContext, ScratchParameters2<Boolean, Boolean>> {

    public static final ScratchParameterTypes2<Boolean, Boolean> PARAMETER_TYPES = new ScratchParameterTypes2<>(PetrolparkScratchTypes.BOOLEAN.get(), PetrolparkScratchTypes.BOOLEAN.get());

    @Override
    public ScratchParameterTypes<ScratchParameters2<Boolean, Boolean>> getParameterTypes() {
        return PARAMETER_TYPES;
    };
    
};
