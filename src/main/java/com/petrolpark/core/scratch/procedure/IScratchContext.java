package com.petrolpark.core.scratch.procedure;

public interface IScratchContext<CONTEXT extends IScratchContext<CONTEXT>> {
    
    public IScratchContextProvider<CONTEXT> holder();

};
