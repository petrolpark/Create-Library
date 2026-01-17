package com.petrolpark.core.scratch.symbol.block.instance;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;

public abstract class NestedProcedureBlockInstance<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> implements INestedProcedureBlockInstance<ENVIRONMENT, INSTANCE> {

    private final ScratchProcedure<ENVIRONMENT, INSTANCE> procedure;

    protected NestedProcedureBlockInstance(ScratchProcedure<ENVIRONMENT, INSTANCE> procedure) {
        this.procedure = procedure;
        procedure().populateContext(self());
    };

    protected abstract INSTANCE self();

    public final ScratchProcedure<ENVIRONMENT, INSTANCE> procedure() {
        return procedure;
    };

};
