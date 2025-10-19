package com.petrolpark.core.scratch.argument;

import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;

public record DropdownArgument<ENVIRONMENT extends IScratchEnvironment, TYPE>(
    int index,
    DropdownParameter<ENVIRONMENT, TYPE> parameter
) implements IScratchArgument<ENVIRONMENT, TYPE> {

    @Override
    public TYPE get(ENVIRONMENT environment) {
        return parameter().values.get(index).value();
    };

    public static class DropdownParameter<ENVIRONMENT extends IScratchEnvironment, TYPE> implements IScratchParameter<ENVIRONMENT, TYPE, DropdownArgument<ENVIRONMENT, TYPE>> {

        private final String key;
        private final List<Named<TYPE>> values;

        private final ContextualCodec<IScratchContextProvider<?>, DropdownArgument<ENVIRONMENT, TYPE>> codec;
        private final ContextualStreamCodec<ByteBuf, IScratchContextProvider<?>, DropdownArgument<ENVIRONMENT, TYPE>> streamCodec;

        public DropdownParameter(String key, List<Named<TYPE>> values) {
            this.key = key;
            this.values = values;
            codec = ContextualCodec.<IScratchContextProvider<?>, Integer>of(Codec.intRange(0, values.size() - 1)).xmap(i -> new DropdownArgument<>(i, this), DropdownArgument::index);
            streamCodec = ContextualStreamCodec.<ByteBuf, IScratchContextProvider<?>, Integer>of(ByteBufCodecs.INT).map(i -> new DropdownArgument<>(i, this), DropdownArgument::index);
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextProvider<?>, DropdownArgument<ENVIRONMENT, TYPE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, DropdownArgument<ENVIRONMENT, TYPE>> argumentStreamCodec() {
            return streamCodec;
        };
        
    };

    public static interface Named<TYPE> {

        public TYPE value();

        public Component name();
    };
    
};
