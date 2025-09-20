package com.petrolpark.core.scratch.procedure;

public interface IScratchContextHolder<CONTEXT extends IScratchContext<CONTEXT>> {
    
    public IScratchContextHolder<?> enclosingContextHolder();
};
