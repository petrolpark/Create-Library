package com.petrolpark.core.scratch.argument;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;
import com.petrolpark.util.Lang;
import com.petrolpark.util.codec.ContextualCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record DropdownArgument<ENVIRONMENT extends IScratchEnvironment, TYPE>(
    int index,
    DropdownParameter<ENVIRONMENT, TYPE> parameter
) implements IScratchArgument<ENVIRONMENT, TYPE> {

    public static final <ENVIRONMENT extends IScratchEnvironment, TYPE> DropdownParameter<ENVIRONMENT, TYPE> dropdownParameter(String key, List<DropdownArgument.Entry<? super ENVIRONMENT, TYPE>> options) {
        return new DropdownParameter<>(key, options);
    };

    public static final <ENVIRONMENT extends IScratchEnvironment, TYPE> DropdownParameter<ENVIRONMENT, TYPE> dropdownParameter(String key, DropdownArgument.Entry<? super ENVIRONMENT, TYPE>[] options) {
        return DropdownArgument.<ENVIRONMENT, TYPE>dropdownParameter(key, List.of(options));
    };

    public static final <ENVIRONMENT extends IScratchEnvironment> DropdownParameter<ENVIRONMENT, Axis> axisParameter(String key) {
        return dropdownParameter(key, DropdownArgument.<ENVIRONMENT>getAxisEntries().toList());
    };

    @Override
    public TYPE get(ENVIRONMENT environment) {
        return parameter().values.get(index()).value(environment);
    };

    @Override
    public boolean canEvaluate() {
        return index() >= 0 && index() < parameter().values.size();
    };

    public static class DropdownParameter<ENVIRONMENT extends IScratchEnvironment, TYPE> implements IScratchParameter<ENVIRONMENT, TYPE, DropdownArgument<ENVIRONMENT, TYPE>> {

        private final String key;
        protected final List<DropdownArgument.Entry<? super ENVIRONMENT, TYPE>> values;

        private final ContextualCodec<IScratchContextProvider<?>, DropdownArgument<ENVIRONMENT, TYPE>> codec;
        private final ContextualStreamCodec<ByteBuf, IScratchContextProvider<?>, DropdownArgument<ENVIRONMENT, TYPE>> streamCodec;

        public DropdownParameter(String key, List<DropdownArgument.Entry<? super ENVIRONMENT, TYPE>> values) {
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

    public static interface Entry<ENVIRONMENT extends IScratchEnvironment, TYPE> {

        public TYPE value(ENVIRONMENT environment);

        @OnlyIn(Dist.CLIENT)
        public Component name(ENVIRONMENT environment);
    };

    public static record SimpleEntry<TYPE>(TYPE value, Component name) implements DropdownArgument.Entry<IScratchEnvironment, TYPE> {

        @Override
        public TYPE value(IScratchEnvironment environment) {
            return value();
        };

        @Override
        public Component name(IScratchEnvironment environment) {
            return name();
        };
    };

    public static final <ENVIRONMENT extends IScratchEnvironment> Stream<DropdownArgument.Entry<? super ENVIRONMENT, Axis>> getAxisEntries() {
        return Stream.of(Axis.values()).<DropdownArgument.Entry<? super ENVIRONMENT, Axis>>map(axis -> new DropdownArgument.SimpleEntry<>(axis, Lang.axis(axis)));
    };
    

};
