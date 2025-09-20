package com.petrolpark.core.scratch;

import java.util.Collection;
import java.util.stream.Stream;

import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;;

public sealed interface ScratchArguments<ENVIRONMENT extends IScratchEnvironment, SIGNATURE extends ScratchSignature>
    extends ScratchSignature
    permits ScratchArguments.None, ScratchArguments.More
{

    public static <ENVIRONMENT extends IScratchEnvironment> ScratchArguments<ENVIRONMENT, ?> of(Collection<IScratchArgument<? super ENVIRONMENT, ?>> arguments) {
        if (arguments.isEmpty()) return new ScratchArguments.None<>();
        if (arguments.size() == 1) return new ScratchArguments.Just<>(arguments.iterator().next());
        ScratchArguments.Builder<ENVIRONMENT> builder = new ScratchArguments.None.Builder<>();
        for (IScratchArgument<? super ENVIRONMENT, ?> argument : arguments) builder = builder.after(argument);
        return builder.build();
    };

    /**
     * Stream of {@link IScratchArgument}s in reverse order.
     */
    public Stream<IScratchArgument<? super ENVIRONMENT, ?>> stream();

    public static sealed interface Builder<ENVIRONMENT extends IScratchEnvironment> permits ScratchArguments.None.Builder, ScratchArguments.More.Builder {

        public <TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> ScratchArguments.More.Builder<ENVIRONMENT> after(ARGUMENT argument);

        public ScratchArguments<ENVIRONMENT, ?> build();
    };

    public static sealed class None<ENVIRONMENT extends IScratchEnvironment> implements ScratchArguments<ENVIRONMENT, ScratchSignature.None>, ScratchSignature.None permits ScratchArguments.None.Builder {

        @Override
        public Stream<IScratchArgument<? super ENVIRONMENT, ?>> stream() {
            return Stream.empty();
        };

        public static final class Builder<ENVIRONMENT extends IScratchEnvironment> extends ScratchArguments.None<ENVIRONMENT> implements ScratchArguments.Builder<ENVIRONMENT> {

            @Override
            public <TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> ScratchArguments.Just.Builder<ENVIRONMENT, TYPE, ARGUMENT> after(ARGUMENT argument) {
                return new ScratchArguments.Just.Builder<>(argument);
            };

            @Override
            public ScratchArguments.None<ENVIRONMENT> build() {
                return new ScratchArguments.None<>();
            };
            
        };

    };

    public static abstract sealed class More<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> 
        implements ScratchArguments<ENVIRONMENT, ScratchSignature.More<TYPE>>, ScratchSignature.More<TYPE> 
        permits Just, And 
    {
    
        protected final ARGUMENT argument;

        protected More(ARGUMENT argument) {
            this.argument = argument;
        };

        public ARGUMENT getArgument() {
            return argument;
        };

        @Override
        public Stream<IScratchArgument<? super ENVIRONMENT, ?>> stream() {
            return Stream.of(argument);
        };

        public TYPE get(ENVIRONMENT environment, IScratchContext<?> context) {
            return argument.get(environment, context);
        };

        public static sealed interface Builder<ENVIRONMENT extends IScratchEnvironment> extends ScratchArguments.Builder<ENVIRONMENT> permits ScratchArguments.Just.Builder, ScratchArguments.And.Builder {};

    };

    public static sealed class Just<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> 
        extends More<ENVIRONMENT, TYPE, ARGUMENT>
        implements ScratchSignature.Just<TYPE>
        permits ScratchArguments.Just.Builder 
    {
        protected Just(ARGUMENT argument) {
            super(argument);
        };

        public static final class Builder<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> extends ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT> implements ScratchArguments.More.Builder<ENVIRONMENT> {
        
            protected Builder(ARGUMENT argument) {
                super(argument);
            };

            @Override
            public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super ENVIRONMENT, PREVIOUS_TYPE>> ScratchArguments.And.Builder<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> after(PREVIOUS_ARGUMENT previousArgument) {
                return new ScratchArguments.And.Builder<>(previousArgument, build());
            };

            @Override
            public ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT> build() {
                return new ScratchArguments.Just<>(argument);
            };

        };

    };

    public static sealed class And<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, NEXT extends ScratchArguments.More<? super ENVIRONMENT, ?, ?>>
        extends More<ENVIRONMENT, TYPE, ARGUMENT> 
        implements ScratchSignature.And<TYPE, NEXT>
        permits ScratchArguments.And.Builder
    {

        protected final NEXT next;

        protected And(ARGUMENT argument, NEXT next) {
            super(argument);
            this.next = next;
        };

        @Override
        public Stream<IScratchArgument<? super ENVIRONMENT, ?>> stream() {
            return Stream.concat(next().stream(), super.stream());
        };
        
        public NEXT next() {
            return next;
        };

        public static final class Builder<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, NEXT extends ScratchArguments.More<? super ENVIRONMENT, ?, ?>> extends ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT> implements ScratchArguments.More.Builder<ENVIRONMENT> {
        
            protected Builder(ARGUMENT argument, NEXT next) {
                super(argument, next);
            };

            @Override
            public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super ENVIRONMENT, PREVIOUS_TYPE>> ScratchArguments.And.Builder<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT>> after(PREVIOUS_ARGUMENT previousArgument) {
                return new ScratchArguments.And.Builder<>(previousArgument, build());
            };

            @Override
            public ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT> build() {
                return new ScratchArguments.And<>(argument, next);
            };

        };

    };
};
