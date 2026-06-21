package petrolpark.mc.library.core.scratch.procedure;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public interface IScratchContextProvider<CONTEXT extends IScratchContext<CONTEXT>> {

    public IScratchEnvironment.Type<?> environmentType();
    
    public IScratchContextProvider<?> enclosingContextProvider();

    public default boolean isRoot() {
        return false;
    };
};
