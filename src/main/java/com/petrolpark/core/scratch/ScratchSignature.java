package com.petrolpark.core.scratch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public sealed interface ScratchSignature<CONTEXT extends IScratchContext, PARAMETERS extends ScratchParameters<CONTEXT>, ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>>
    extends ScratchParameters<CONTEXT>
    permits ScratchSignature.None, ScratchSignature.More
{
    public Codec<ARGUMENTS> argumentsCodec();

    public StreamCodec<? super RegistryFriendlyByteBuf, ARGUMENTS> argumentsStreamCodec();

    public static sealed interface Builder<CONTEXT extends IScratchContext> permits ScratchSignature.None.Builder, ScratchSignature.More.Builder {

        public <TYPE> ScratchSignature.More.Builder<CONTEXT> after(IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType);

        public ScratchSignature<CONTEXT, ?, ?> build();
    };

    public static sealed class None<CONTEXT extends IScratchContext> implements ScratchSignature<CONTEXT, ScratchParameters.None<CONTEXT>, ScratchArguments.None<CONTEXT>>, ScratchParameters.None<CONTEXT> permits ScratchSignature.None.Builder {

        private final ScratchArguments.None<CONTEXT> noneArgumentsInstance = new ScratchArguments.None<>();
        private final Codec<ScratchArguments.None<CONTEXT>> argumentsCodec = Codec.unit(noneArgumentsInstance);
        private final StreamCodec<ByteBuf, ScratchArguments.None<CONTEXT>> argumentsStreamCodec = StreamCodec.unit(noneArgumentsInstance);

        @Override
        public Codec<ScratchArguments.None<CONTEXT>> argumentsCodec() {
            return argumentsCodec;
        };

        @Override
        public StreamCodec<ByteBuf, ScratchArguments.None<CONTEXT>> argumentsStreamCodec() {
            return argumentsStreamCodec;
        };

        public static final class Builder<CONTEXT extends IScratchContext> extends ScratchSignature.None<CONTEXT> implements ScratchSignature.Builder<CONTEXT> {

            @Override
            public <TYPE> ScratchSignature.Just.Builder<CONTEXT, TYPE> after(IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType) {
                return new ScratchSignature.Just.Builder<>(argumentType);
            };

            @Override
            public ScratchSignature.None<CONTEXT> build() {
                return new ScratchSignature.None<>();
            };
            
        };

    };

    public static abstract sealed class More<CONTEXT extends IScratchContext, TYPE, ARGUMENTS extends ScratchArguments.More<CONTEXT, TYPE>> 
        implements ScratchSignature<CONTEXT, ScratchParameters.More<CONTEXT, TYPE>, ARGUMENTS>, ScratchParameters.More<CONTEXT, TYPE> 
        permits Just, And 
    {
    
        protected final IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType;

        protected More(IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType) {
            this.argumentType = argumentType;
        };

        @Override
        public abstract Codec<ARGUMENTS> argumentsCodec();

        @Override
        public abstract StreamCodec<? super RegistryFriendlyByteBuf, ARGUMENTS> argumentsStreamCodec();

        public static sealed interface Builder<CONTEXT extends IScratchContext> extends ScratchSignature.Builder<CONTEXT> permits ScratchSignature.Just.Builder, ScratchSignature.And.Builder {};

    };

    public static sealed class Just<CONTEXT extends IScratchContext, TYPE> 
        extends More<CONTEXT, TYPE, ScratchArguments.Just<CONTEXT, TYPE>>
        implements ScratchParameters.Just<CONTEXT, TYPE>
        permits ScratchSignature.Just.Builder 
    {
        private final Codec<ScratchArguments.Just<CONTEXT, TYPE>> argumentsCodec = argumentType.codec().xmap(ScratchArguments.Just::new, ScratchArguments.Just::getArgument);
        private final StreamCodec<? super RegistryFriendlyByteBuf, ScratchArguments.Just<CONTEXT, TYPE>> argumentsStreamCodec = argumentType.streamCodec().map(ScratchArguments.Just::new, ScratchArguments.Just::getArgument);

        protected Just(IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType) {
            super(argumentType);
        };

        @Override
        public Codec<ScratchArguments.Just<CONTEXT, TYPE>> argumentsCodec() {
            return argumentsCodec;
        };

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ScratchArguments.Just<CONTEXT, TYPE>> argumentsStreamCodec() {
            return argumentsStreamCodec;
        };

        public static final class Builder<CONTEXT extends IScratchContext, TYPE> extends ScratchSignature.Just<CONTEXT, TYPE> implements ScratchSignature.More.Builder<CONTEXT> {
        
            protected Builder(IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType) {
                super(argumentType);
            };

            @Override
            public <PREVIOUS_TYPE> ScratchSignature.And.Builder<CONTEXT, PREVIOUS_TYPE, ScratchArguments.Just<CONTEXT, TYPE>, ScratchSignature.Just<CONTEXT, TYPE>> after(IScratchArgument.Type<CONTEXT, PREVIOUS_TYPE, IScratchArgument<? super CONTEXT, PREVIOUS_TYPE>> argumentType) {
                return new ScratchSignature.And.Builder<>(argumentType, build());
            };

            @Override
            public ScratchSignature.Just<CONTEXT, TYPE> build() {
                return new ScratchSignature.Just<>(argumentType);
            };

        };

    };

    public static sealed class And<
            CONTEXT extends IScratchContext, TYPE, NEXT_ARGUMENTS extends ScratchArguments.More<CONTEXT, ?>, NEXT extends ScratchSignature.More<CONTEXT, ?, NEXT_ARGUMENTS>
        > extends More<CONTEXT, TYPE, ScratchArguments.And<CONTEXT, TYPE, NEXT_ARGUMENTS>> 
        implements ScratchParameters.And<CONTEXT, TYPE, NEXT>
        permits ScratchSignature.And.Builder
    {
        protected final NEXT next;

        private final Codec<ScratchArguments.And<CONTEXT, TYPE, NEXT_ARGUMENTS>> argumentsCodec = RecordCodecBuilder.create(instance -> instance.group(
            argumentType.codec().fieldOf("argument").forGetter(ScratchArguments.And::getArgument),
            next().argumentsCodec().fieldOf("next").forGetter(ScratchArguments.And::next)
        ).apply(instance, ScratchArguments.And::new));

        private final StreamCodec<? super RegistryFriendlyByteBuf, ScratchArguments.And<CONTEXT, TYPE, NEXT_ARGUMENTS>> argumentsStreamCodec  = StreamCodec.composite(
            argumentType.streamCodec(), ScratchArguments.And::getArgument,
            next().argumentsStreamCodec(), ScratchArguments.And::next,
            ScratchArguments.And::new
        );

        protected And(IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType, NEXT next) {
            super(argumentType);
            this.next = next;
        };

        @Override
        public Codec<ScratchArguments.And<CONTEXT, TYPE, NEXT_ARGUMENTS>> argumentsCodec() {
            return argumentsCodec;
        };

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ScratchArguments.And<CONTEXT, TYPE, NEXT_ARGUMENTS>> argumentsStreamCodec() {
            return argumentsStreamCodec;
        };

        public NEXT next() {
            return next;
        };

        public static final class Builder<CONTEXT extends IScratchContext, TYPE, NEXT_ARGUMENTS extends ScratchArguments.More<CONTEXT, ?>, NEXT extends ScratchSignature.More<CONTEXT, ?, NEXT_ARGUMENTS>> extends ScratchSignature.And<CONTEXT, TYPE, NEXT_ARGUMENTS, NEXT> implements ScratchSignature.More.Builder<CONTEXT> {
        
            protected Builder(IScratchArgument.Type<CONTEXT, TYPE, IScratchArgument<? super CONTEXT, TYPE>> argumentType, NEXT next) {
                super(argumentType, next);
            };

            @Override
            public <PREVIOUS_TYPE> ScratchSignature.And.Builder<CONTEXT, PREVIOUS_TYPE, ScratchArguments.And<CONTEXT, TYPE, NEXT_ARGUMENTS>, ScratchSignature.And<CONTEXT, TYPE, NEXT_ARGUMENTS, NEXT>> after(IScratchArgument.Type<CONTEXT, PREVIOUS_TYPE, IScratchArgument<? super CONTEXT, PREVIOUS_TYPE>> argumentType) {
                return new ScratchSignature.And.Builder<>(argumentType, build());
            };

            @Override
            public ScratchSignature.And<CONTEXT, TYPE, NEXT_ARGUMENTS, NEXT> build() {
                return new ScratchSignature.And<>(argumentType, next);
            };

        };

    };
};
