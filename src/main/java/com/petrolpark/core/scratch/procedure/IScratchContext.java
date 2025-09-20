package com.petrolpark.core.scratch.procedure;

public interface IScratchContext<CONTEXT extends IScratchContext<CONTEXT>> {
    
    public IScratchContextHolder<CONTEXT> holder();

    public IScratchContext<?> enclosingContext();

    @SuppressWarnings("unchecked")
    public default <TARGET_CONTEXT extends IScratchContext<TARGET_CONTEXT>> TARGET_CONTEXT get(IScratchContextHolder<TARGET_CONTEXT> holder) {
        if (holder == holder()) return (TARGET_CONTEXT)this;
        return enclosingContext().get(holder);
    };
};
