package com.petrolpark.core.scratch.argument;

import com.mojang.serialization.Codec;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LiteralArgument<TYPE>(TYPE value, LiteralParameter<TYPE> parameter) implements IScratchArgument<IScratchEnvironment, TYPE> {
    
    @Override
    public TYPE get(IScratchEnvironment context, IScratchContext<?> scope) {
        return value();
    };

    public static final LiteralParameter<Integer> intLiteral(String key) {
        return new LiteralParameter<>(key, Codec.INT, ByteBufCodecs.INT);
    };

    public static class LiteralParameter<TYPE> implements IScratchParameter<IScratchEnvironment, TYPE, LiteralArgument<TYPE>> {

        private final String key;
        private final ContextualCodec<IScratchContextHolder<?>, LiteralArgument<TYPE>> codec;
        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, LiteralArgument<TYPE>> streamCodec;
        
        public LiteralParameter(String key, Codec<TYPE> codec, StreamCodec<? super RegistryFriendlyByteBuf, TYPE> streamCodec) {
            this.key = key;
            this.codec = ContextualCodec.of(codec.xmap(value -> new LiteralArgument<>(value, this), LiteralArgument::value));
            this.streamCodec = ContextualStreamCodec.of(streamCodec.map(value -> new LiteralArgument<>(value, this), LiteralArgument::value));
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextHolder<?>, LiteralArgument<TYPE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, LiteralArgument<TYPE>> argumentStreamCodec() {
            return streamCodec;
        };

    };
};
