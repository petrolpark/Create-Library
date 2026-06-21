package petrolpark.mc.library.core.scratch.symbol;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class ScratchSymbol<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> implements IScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> {
    
    public final PARAMETERS parameters;

    protected ScratchSymbol(PARAMETERS parameters) {
        this.parameters = parameters;
    };

    @Override
    public final PARAMETERS getParameters() {
        return parameters;
    };

};
