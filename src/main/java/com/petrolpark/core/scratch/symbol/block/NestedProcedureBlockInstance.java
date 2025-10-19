package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;

public interface NestedProcedureBlockInstance<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> extends IScratchBlockInstance<ENVIRONMENT>, IScratchContext<INSTANCE> {
    
};
