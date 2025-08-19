package com.petrolpark.core.scratch.type;

import com.petrolpark.core.scratch.ScratchParameters.ScratchParameters1;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

public interface IScratchType<TYPE> {
    
    public Class<TYPE> getTypeClass();

    public default <CONTEXT extends IScratchContext, OTHER_TYPE> IScratchExpression<? super CONTEXT, ? extends OTHER_TYPE, ? extends ScratchParameters1<TYPE>> getCastExpression(CONTEXT context, IScratchType<? super OTHER_TYPE> otherType) {
        return null;
    };
};
