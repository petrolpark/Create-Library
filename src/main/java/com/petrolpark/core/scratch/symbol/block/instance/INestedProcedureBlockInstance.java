package com.petrolpark.core.scratch.symbol.block.instance;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.symbol.block.IScratchBlockInstance;

public interface INestedProcedureBlockInstance<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends INestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> extends IScratchBlockInstance<ENVIRONMENT>, IScratchContext<INSTANCE> {
    
};
