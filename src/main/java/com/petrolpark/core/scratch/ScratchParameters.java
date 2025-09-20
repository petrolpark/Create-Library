package com.petrolpark.core.scratch;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.codec.RecordContextualCodecBuilder;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

public sealed interface ScratchParameters<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>>
    extends ScratchSignature
    permits ScratchParameters.None, ScratchParameters.More
{
    public ContextualCodec<IScratchContextHolder<?>, ARGUMENTS> argumentsCodec();

    public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ARGUMENTS> argumentsStreamCodec();

    public static <ENVIRONMENT extends IScratchEnvironment> ScratchParameters.None.Builder<ENVIRONMENT> parameters() {
        return new ScratchParameters.None.Builder<>();
    };

    public static sealed class None<ENVIRONMENT extends IScratchEnvironment> implements ScratchParameters<ENVIRONMENT, ScratchArguments.None<ENVIRONMENT>>, ScratchSignature.None permits ScratchParameters.None.Builder {

        private final ScratchArguments.None<ENVIRONMENT> noneArgumentsInstance = new ScratchArguments.None<>();
        private final ContextualCodec<IScratchContextHolder<?>, ScratchArguments.None<ENVIRONMENT>> argumentsCodec = ContextualCodec.unit(noneArgumentsInstance);
        private final ContextualStreamCodec<ByteBuf, IScratchContextHolder<?>, ScratchArguments.None<ENVIRONMENT>> argumentsStreamCodec = ContextualStreamCodec.unit(noneArgumentsInstance);

        @Override
        public ContextualCodec<IScratchContextHolder<?>, ScratchArguments.None<ENVIRONMENT>> argumentsCodec() {
            return argumentsCodec;
        };

        @Override
        public ContextualStreamCodec<ByteBuf, IScratchContextHolder<?>, ScratchArguments.None<ENVIRONMENT>> argumentsStreamCodec() {
            return argumentsStreamCodec;
        };

        public static final class Builder<ENVIRONMENT extends IScratchEnvironment> extends ScratchParameters.None<ENVIRONMENT> {

            public <TYPE, ARGUMENT extends IScratchArgument<ENVIRONMENT, TYPE>> ScratchParameters.Just.Builder<ENVIRONMENT, TYPE, ARGUMENT> after(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
                return new ScratchParameters.Just.Builder<>(parameter);
            };

            public ScratchParameters.None<ENVIRONMENT> build() {
                return new ScratchParameters.None<>();
            };
            
        };

    };

    public static abstract sealed class More<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, TYPE, ARGUMENT>> 
        implements ScratchParameters<ENVIRONMENT, ARGUMENTS>, ScratchSignature.More<TYPE> 
        permits Just, And 
    {
    
        protected final IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter;

        protected More(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
            this.parameter = parameter;
        };

        public ContextualCodec<IScratchContextHolder<?>, ARGUMENT> argumentCodec() {
            return parameter.argumentCodec();
        };

        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ARGUMENT> argumentStreamCodec() {
            return parameter.argumentStreamCodec();
        };

        @Override
        public abstract ContextualCodec<IScratchContextHolder<?>, ARGUMENTS> argumentsCodec();

        @Override
        public abstract ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ARGUMENTS> argumentsStreamCodec();

    };

    public static sealed class Just<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> 
        extends More<ENVIRONMENT, TYPE, ARGUMENT, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>>
        implements ScratchSignature.Just<TYPE>
        permits ScratchParameters.Just.Builder 
    {
        private final ContextualCodec<IScratchContextHolder<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsCodec = argumentCodec().xmap(ScratchArguments.Just::new, ScratchArguments.Just::argument);
        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsStreamCodec = argumentStreamCodec().map(ScratchArguments.Just::new, ScratchArguments.Just::argument);

        protected Just(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
            super(parameter);
        };

        @Override
        public ContextualCodec<IScratchContextHolder<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsCodec() {
            return argumentsCodec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsStreamCodec() {
            return argumentsStreamCodec;
        };

        public static final class Builder<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> extends ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT> {
        
            protected Builder(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter) {
                super(parameter);
            };

            public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super ENVIRONMENT, PREVIOUS_TYPE>> ScratchParameters.And.Builder<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT>> after(IScratchParameter<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT> parameter) {
                return new ScratchParameters.And.Builder<>(parameter, build());
            };

            public ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT> build() {
                return new ScratchParameters.Just<>(parameter);
            };

        };

    };

    public static sealed class And<
            ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, NEXT_ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, ?, ?>, NEXT extends ScratchParameters.More<ENVIRONMENT, ?, ?, NEXT_ARGUMENTS>
        > extends More<ENVIRONMENT, TYPE, ARGUMENT, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> 
        implements ScratchSignature.And<TYPE, NEXT>
        permits ScratchParameters.And.Builder
    {
        protected final NEXT next;

        private final ContextualCodec<IScratchContextHolder<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsCodec = RecordContextualCodecBuilder.create(instance -> instance.group(
            argumentCodec().fieldOf("argument").forGetter(ScratchArguments.And::argument),
            next().argumentsCodec().fieldOf("next").forGetter(ScratchArguments.And::next)
        ).apply(instance, ScratchArguments.And::new));

        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsStreamCodec = ContextualStreamCodec.composite(
            argumentStreamCodec(), ScratchArguments.And::argument,
            next().argumentsStreamCodec(), ScratchArguments.And::next,
            ScratchArguments.And::new
        );

        protected And(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter, NEXT next) {
            super(parameter);
            this.next = next;
        };

        @Override
        public ContextualCodec<IScratchContextHolder<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsCodec() {
            return argumentsCodec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsStreamCodec() {
            return argumentsStreamCodec;
        };

        public NEXT next() {
            return next;
        };

        public static final class Builder<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, NEXT_ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, ?, ?>, NEXT extends ScratchParameters.More<ENVIRONMENT, ?, ?, NEXT_ARGUMENTS>> extends ScratchParameters.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS, NEXT> {
        
            protected Builder(IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT> parameter, NEXT next) {
                super(parameter, next);
            };

            public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super ENVIRONMENT, PREVIOUS_TYPE>> ScratchParameters.And.Builder<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>, ScratchParameters.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS, NEXT>> after(IScratchParameter<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT> parameter) {
                return new ScratchParameters.And.Builder<>(parameter, build());
            };

            public ScratchParameters.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS, NEXT> build() {
                return new ScratchParameters.And<>(parameter, next);
            };

        };

    };
};
