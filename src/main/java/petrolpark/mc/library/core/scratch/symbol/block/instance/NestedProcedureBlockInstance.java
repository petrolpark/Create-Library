package petrolpark.mc.library.core.scratch.symbol.block.instance;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.ScratchProcedure;

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
