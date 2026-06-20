package com.petrolpark.core.scratch.argument;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;
import com.petrolpark.util.codec.ContextualCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LiteralArgument<TYPE>(TYPE value, LiteralParameter<TYPE> parameter) implements IScratchArgument<IScratchEnvironment, TYPE> {
    
    @Override
    public TYPE get(IScratchEnvironment context) {
        return value();
    };

    @Override
    public boolean canEvaluate() {
        return true;
    };

    public static final LiteralParameter<ScratchVariableIdentifier> variable(String key) {
        return new LiteralParameter<>(key, ScratchVariableIdentifier.CODEC, ScratchVariableIdentifier.STREAM_CODEC);
    };

    public static final LiteralParameter<Integer> intLiteral(String key) {
        return new LiteralParameter<>(key, Codec.INT, ByteBufCodecs.INT);
    };

    public static class LiteralParameter<TYPE> implements IScratchParameter<IScratchEnvironment, TYPE, LiteralArgument<TYPE>> {

        private final String key;
        private final ContextualCodec<IScratchContextProvider<?>, LiteralArgument<TYPE>> codec;
        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, LiteralArgument<TYPE>> streamCodec;
        
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
        public ContextualCodec<IScratchContextProvider<?>, LiteralArgument<TYPE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, LiteralArgument<TYPE>> argumentStreamCodec() {
            return streamCodec;
        };

    };
};
