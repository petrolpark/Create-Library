package com.petrolpark.core.scratch;

public sealed interface ScratchSignature permits ScratchSignature.None, ScratchSignature.More, ScratchArguments, ScratchParameters {
    
    public sealed interface None extends ScratchSignature permits ScratchArguments.None, ScratchParameters.None {};

    public sealed interface More<TYPE> extends ScratchSignature permits Just, And, ScratchArguments.More, ScratchParameters.More {};

    public sealed interface Just<TYPE> extends More<TYPE> permits ScratchArguments.Just, ScratchParameters.Just {};

    public sealed interface And<TYPE, NEXT extends More<?>> extends More<TYPE> permits ScratchArguments.And, ScratchParameters.And {};
};
