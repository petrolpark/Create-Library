package petrolpark.mc.library.core.scratch.symbol.block.instance;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.IScratchContext;
import petrolpark.mc.library.core.scratch.symbol.block.IScratchBlockInstance;

public interface INestedProcedureBlockInstance<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends INestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> extends IScratchBlockInstance<ENVIRONMENT>, IScratchContext<INSTANCE> {
    
};
