package com.petrolpark.core.scratch;

import java.util.Collection;
import java.util.stream.Stream;

public sealed interface ScratchArguments<CONTEXT extends IScratchContext, PARAMETERS extends ScratchParameters<CONTEXT>>
    extends ScratchParameters<CONTEXT>
    permits ScratchArguments.None, ScratchArguments.More
{

    public static <CONTEXT extends IScratchContext> ScratchArguments<CONTEXT, ?> of(Collection<IScratchArgument<? super CONTEXT, ?>> arguments) {
        if (arguments.isEmpty()) return new ScratchArguments.None<>();
        if (arguments.size() == 1) return new ScratchArguments.Just<>(arguments.iterator().next());
        ScratchArguments.Builder<CONTEXT> builder = new ScratchArguments.None.Builder<>();
        for (IScratchArgument<? super CONTEXT, ?> argument : arguments) builder = builder.after(argument);
        return builder.build();
    };

    public PARAMETERS asParameters();

    /**
     * Stream of {@link IScratchArgument}s in reverse order.
     */
    public Stream<IScratchArgument<? super CONTEXT, ?>> stream();

    public static sealed interface Builder<CONTEXT extends IScratchContext> permits ScratchArguments.None.Builder, ScratchArguments.More.Builder {

        public <TYPE> ScratchArguments.More.Builder<CONTEXT> after(IScratchArgument<? super CONTEXT, TYPE> argument);

        public ScratchArguments<CONTEXT, ?> build();
    };

    public static sealed class None<CONTEXT extends IScratchContext> implements ScratchArguments<CONTEXT, ScratchParameters.None<CONTEXT>>, ScratchParameters.None<CONTEXT> permits ScratchArguments.None.Builder {

        @Override
        public ScratchParameters.None<CONTEXT> asParameters() {
            return this;
        };

        @Override
        public Stream<IScratchArgument<? super CONTEXT, ?>> stream() {
            return Stream.empty();
        };

        public static final class Builder<CONTEXT extends IScratchContext> extends ScratchArguments.None<CONTEXT> implements ScratchArguments.Builder<CONTEXT> {

            @Override
            public <TYPE> ScratchArguments.Just.Builder<CONTEXT, TYPE> after(IScratchArgument<? super CONTEXT, TYPE> argument) {
                return new ScratchArguments.Just.Builder<CONTEXT, TYPE>(argument);
            };

            @Override
            public ScratchArguments.None<CONTEXT> build() {
                return new ScratchArguments.None<>();
            };
            
        };

    };

    public static abstract sealed class More<CONTEXT extends IScratchContext, TYPE> 
        implements ScratchArguments<CONTEXT, ScratchParameters.More<CONTEXT, TYPE>>, ScratchParameters.More<CONTEXT, TYPE> 
        permits Just, And 
    {
    
        protected final IScratchArgument<? super CONTEXT, TYPE> argument;

        protected More(IScratchArgument<? super CONTEXT, TYPE> argument) {
            this.argument = argument;
        };

        protected IScratchArgument<? super CONTEXT, TYPE> getArgument() {
            return argument;
        };

        @Override
        public Stream<IScratchArgument<? super CONTEXT, ?>> stream() {
            return Stream.of(argument);
        };

        public TYPE get(CONTEXT context) {
            return argument.get(context);
        };

        public static sealed interface Builder<CONTEXT extends IScratchContext> extends ScratchArguments.Builder<CONTEXT> permits ScratchArguments.Just.Builder, ScratchArguments.And.Builder {};

    };

    public static sealed class Just<CONTEXT extends IScratchContext, TYPE> 
        extends More<CONTEXT, TYPE>
        implements ScratchParameters.Just<CONTEXT, TYPE>
        permits ScratchArguments.Just.Builder 
    {
        protected Just(IScratchArgument<? super CONTEXT, TYPE> argument) {
            super(argument);
        };

        @Override
        public ScratchParameters.Just<CONTEXT, TYPE> asParameters() {
            return this;
        };

        public static final class Builder<CONTEXT extends IScratchContext, TYPE> extends ScratchArguments.Just<CONTEXT, TYPE> implements ScratchArguments.More.Builder<CONTEXT> {
        
            protected Builder(IScratchArgument<? super CONTEXT, TYPE> argument) {
                super(argument);
            };

            @Override
            public <PREVIOUS_TYPE> ScratchArguments.And.Builder<CONTEXT, PREVIOUS_TYPE, ScratchArguments.Just<CONTEXT, TYPE>> after(IScratchArgument<? super CONTEXT, PREVIOUS_TYPE> previousArgument) {
                return new ScratchArguments.And.Builder<>(previousArgument, build());
            };

            @Override
            public ScratchArguments.Just<CONTEXT, TYPE> build() {
                return new ScratchArguments.Just<>(argument);
            };

        };

    };

    public static sealed class And<CONTEXT extends IScratchContext, TYPE, NEXT extends ScratchArguments.More<? super CONTEXT, ?>>
        extends More<CONTEXT, TYPE> 
        implements ScratchParameters.And<CONTEXT, TYPE, NEXT>
        permits ScratchArguments.And.Builder
    {

        protected final NEXT next;

        protected And(IScratchArgument<? super CONTEXT, TYPE> argument, NEXT next) {
            super(argument);
            this.next = next;
        };

        @Override
        public ScratchParameters.And<CONTEXT, TYPE, NEXT> asParameters() {
            return this;
        };

        @Override
        public Stream<IScratchArgument<? super CONTEXT, ?>> stream() {
            return Stream.concat(next().stream(), super.stream());
        };
        
        public NEXT next() {
            return next;
        };

        public static final class Builder<CONTEXT extends IScratchContext, TYPE, NEXT extends ScratchArguments.More<? super CONTEXT, ?>> extends ScratchArguments.And<CONTEXT, TYPE, NEXT> implements ScratchArguments.More.Builder<CONTEXT> {
        
            protected Builder(IScratchArgument<? super CONTEXT, TYPE> argument, NEXT next) {
                super(argument, next);
            };

            @Override
            public <PREVIOUS_TYPE> ScratchArguments.And.Builder<CONTEXT, PREVIOUS_TYPE, ScratchArguments.And<CONTEXT, TYPE, NEXT>> after(IScratchArgument<? super CONTEXT, PREVIOUS_TYPE> previousArgument) {
                return new ScratchArguments.And.Builder<>(previousArgument, build());
            };

            @Override
            public ScratchArguments.And<CONTEXT, TYPE, NEXT> build() {
                return new ScratchArguments.And<>(argument, next);
            };

        };

    };
};
