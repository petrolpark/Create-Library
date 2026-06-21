package petrolpark.mc.library.core.scratch.argument;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public interface IScratchArgument<ENVIRONMENT extends IScratchEnvironment, TYPE> {
    
    public TYPE get(ENVIRONMENT environment);

    /**
     * Whether this Argument can evaluate without crashing.
     */
    public boolean canEvaluate();

    public IScratchParameter<? super ENVIRONMENT, TYPE, ? extends IScratchArgument<? super ENVIRONMENT, TYPE>> parameter();
};
