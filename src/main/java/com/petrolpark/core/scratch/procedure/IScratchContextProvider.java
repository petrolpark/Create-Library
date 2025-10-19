package com.petrolpark.core.scratch.procedure;

public interface IScratchContextProvider<CONTEXT extends IScratchContext<CONTEXT>> {
    
    public IScratchContextProvider<?> enclosingContextProvider();
};
