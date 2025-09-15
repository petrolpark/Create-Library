package com.petrolpark.core.scratch;

public sealed interface ScratchSignature<CONTEXT extends IScratchContext, PARAMETERS extends ScratchParameters<? super CONTEXT>>
    extends ScratchParameters<CONTEXT>
    permits ScratchSignature.None, ScratchSignature.More
{

    public static sealed interface Builder<CONTEXT extends IScratchContext> permits ScratchSignature.None.Builder, ScratchSignature.More.Builder {

        public <TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>> ScratchSignature.More.Builder<CONTEXT> after(IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType);

        public ScratchSignature<CONTEXT, ?> build();
    };

    public static sealed class None<CONTEXT extends IScratchContext> implements ScratchSignature<CONTEXT, ScratchParameters.None<CONTEXT>>, ScratchParameters.None<CONTEXT> permits ScratchSignature.None.Builder {

        public static final class Builder<CONTEXT extends IScratchContext> extends ScratchSignature.None<CONTEXT> implements ScratchSignature.Builder<CONTEXT> {

            @Override
            public <TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>> ScratchSignature.Just.Builder<CONTEXT, TYPE, ARGUMENT> after(IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType) {
                return new ScratchSignature.Just.Builder<>(argumentType);
            };

            @Override
            public ScratchSignature.None<CONTEXT> build() {
                return new ScratchSignature.None<>();
            };
            
        };

    };

    public static abstract sealed class More<CONTEXT extends IScratchContext, TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>> 
        implements ScratchSignature<CONTEXT, ScratchParameters.More<CONTEXT, TYPE>>, ScratchParameters.More<CONTEXT, TYPE> 
        permits Just, And 
    {
    
        protected final IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType;

        protected More(IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType) {
            this.argumentType = argumentType;
        };

        @Override
        @Deprecated
        public TYPE get(CONTEXT context) {
            return null;
        };

        public static sealed interface Builder<CONTEXT extends IScratchContext> extends ScratchSignature.Builder<CONTEXT> permits ScratchSignature.Just.Builder, ScratchSignature.And.Builder {};

    };

    public static sealed class Just<CONTEXT extends IScratchContext, TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>> 
        extends More<CONTEXT, TYPE, ARGUMENT>
        implements ScratchParameters.Just<CONTEXT, TYPE>
        permits ScratchSignature.Just.Builder 
    {
        protected Just(IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType) {
            super(argumentType);
        };

        public static final class Builder<CONTEXT extends IScratchContext, TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>> extends ScratchSignature.Just<CONTEXT, TYPE, ARGUMENT> implements ScratchSignature.More.Builder<CONTEXT> {
        
            protected Builder(IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType) {
                super(argumentType);
            };

            @Override
            public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super CONTEXT, PREVIOUS_TYPE>> ScratchSignature.And.Builder<CONTEXT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, ScratchSignature.Just<CONTEXT, TYPE, ARGUMENT>> after(IScratchArgument.Type<? super CONTEXT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT> previousArgumentType) {
                return new ScratchSignature.And.Builder<>(previousArgumentType, build());
            };

            @Override
            public ScratchSignature.Just<CONTEXT, TYPE, ARGUMENT> build() {
                return new ScratchSignature.Just<>(argumentType);
            };

        };

    };

    public static sealed class And<CONTEXT extends IScratchContext, TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>, NEXT extends ScratchSignature.More<? super CONTEXT, ?, ?>>
        extends More<CONTEXT, TYPE, ARGUMENT> 
        implements ScratchParameters.And<CONTEXT, TYPE, NEXT>
        permits ScratchSignature.And.Builder
    {

        protected final NEXT next;

        protected And(IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType, NEXT next) {
            super(argumentType);
            this.next = next;
        };

        @Override
        public NEXT next() {
            return next;
        };

        public static final class Builder<CONTEXT extends IScratchContext, TYPE, ARGUMENT extends IScratchArgument<? super CONTEXT, TYPE>, NEXT extends ScratchSignature.More<? super CONTEXT, ?, ?>> extends ScratchSignature.And<CONTEXT, TYPE, ARGUMENT, NEXT> implements ScratchSignature.More.Builder<CONTEXT> {
        
            protected Builder(IScratchArgument.Type<? super CONTEXT, TYPE, ARGUMENT> argumentType, NEXT next) {
                super(argumentType, next);
            };

            @Override
            public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super CONTEXT, PREVIOUS_TYPE>> ScratchSignature.And.Builder<CONTEXT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, ScratchSignature.And<CONTEXT, TYPE, ARGUMENT, NEXT>> after(IScratchArgument.Type<? super CONTEXT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT> previousArgumentType) {
                return new ScratchSignature.And.Builder<>(previousArgumentType, build());
            };

            @Override
            public ScratchSignature.And<CONTEXT, TYPE, ARGUMENT, NEXT> build() {
                return new ScratchSignature.And<>(argumentType, next);
            };

        };

    };
};
