package com.petrolpark.core.scratch;

public sealed interface ScratchParameters<CONTEXT extends IScratchContext> permits ScratchParameters.None, ScratchParameters.More, ScratchArguments, ScratchSignature {
    
    public sealed interface None<CONTEXT extends IScratchContext> extends ScratchParameters<CONTEXT> permits ScratchArguments.None, ScratchSignature.None {};

    public sealed interface More<CONTEXT extends IScratchContext, TYPE> extends ScratchParameters<CONTEXT> permits Just, And, ScratchArguments.More, ScratchSignature.More {
        public TYPE get(CONTEXT context);
    };

    public sealed interface Just<CONTEXT extends IScratchContext, TYPE> extends More<CONTEXT, TYPE> permits ScratchArguments.Just, ScratchSignature.Just {};

    public sealed interface And<CONTEXT extends IScratchContext, TYPE, NEXT extends More<? super CONTEXT, ?>> extends More<CONTEXT, TYPE> permits ScratchArguments.And, ScratchSignature.And {
        public NEXT next();
    };
};
