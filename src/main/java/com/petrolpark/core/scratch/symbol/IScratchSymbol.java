package com.petrolpark.core.scratch.symbol;

import com.petrolpark.core.scratch.ScratchParameterTypes;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.type.IScratchSymbolType;

public interface IScratchSymbol<CONTEXT extends IScratchContext, PARAMETERS extends ScratchParameters> {

    public ScratchParameterTypes<PARAMETERS> getParameterTypes();

    public IScratchSymbolType<? extends IScratchSymbol<CONTEXT, PARAMETERS>> getSymbolType();
};
