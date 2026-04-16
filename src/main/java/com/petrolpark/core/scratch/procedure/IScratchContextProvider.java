package com.petrolpark.core.scratch.procedure;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public interface IScratchContextProvider<CONTEXT extends IScratchContext<CONTEXT>> {

    public IScratchEnvironment.Type<?> environmentType();
    
    public IScratchContextProvider<?> enclosingContextProvider();

    public default boolean isRoot() {
        return false;
    };
};
