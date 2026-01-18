package com.petrolpark.core.scratch.argument;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public class ContextArgument<ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> implements IScratchArgument<ENVIRONMENT, CONTEXT>, IScratchContextHolder {
    
    protected final ContextParameter<ENVIRONMENT, CONTEXT> parameter;
    protected final IScratchContextProvider<CONTEXT> contextProvider;
    protected CONTEXT context;

    public static final <ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> ContextParameter<ENVIRONMENT, CONTEXT> contextParameter(String key) {
        return new ContextParameter<>(key);  
    };

    public ContextArgument(ContextParameter<ENVIRONMENT, CONTEXT> parameter, IScratchContextProvider<CONTEXT> contextProvider) {
        this.parameter = parameter;
        this.contextProvider = contextProvider;
    };

    @Override
    @SuppressWarnings("unchecked")
    public <NEW_CONTEXT extends IScratchContext<NEW_CONTEXT>> void populateContext(IScratchContextProvider<NEW_CONTEXT> contextProvider, NEW_CONTEXT context) {
        if (context != null) throw new IllegalStateException("Already populated context");
        if (contextProvider == this.contextProvider) this.context = (CONTEXT)context;
    };

    @Override
    public CONTEXT get(ENVIRONMENT environment) {
        return context;
    };

    @Override
    public boolean canEvaluate() {
        return context != null;
    };

    @Override
    public ContextParameter<ENVIRONMENT, CONTEXT> parameter() {
        return parameter;
    };

    public static class ContextParameter<ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> implements IScratchParameter<ENVIRONMENT, CONTEXT, ContextArgument<ENVIRONMENT, CONTEXT>> {

        protected final String key;

        @SuppressWarnings("unchecked")
        protected final ContextualCodec<IScratchContextProvider<?>, ContextArgument<ENVIRONMENT, CONTEXT>> codec = ContextualCodec.<IScratchContextProvider<?>, Integer>of(Codec.INT).flatContextualXmap(
            (contextProvider, nesting) -> {
                for (int i = 0; i < nesting; i++) contextProvider = contextProvider.enclosingContextProvider();
                try {
                    return DataResult.success(new ContextArgument<>(this, (IScratchContextProvider<CONTEXT>)contextProvider));
                } catch (ClassCastException e) {};
                return DataResult.error(() -> "Could not find required context");
            }, (contextProvider, argument) -> {
                int nesting = 0;
                try {
                    while (contextProvider != argument.contextProvider && nesting < 255) {
                        contextProvider = contextProvider.enclosingContextProvider();
                        nesting++;
                    };
                    if (nesting >= 255) return DataResult.error(() -> "Timed out trying to find context");
                } catch (IllegalStateException e) {
                    return DataResult.error(() -> "Could not find required context");
                };
                return DataResult.success(nesting);
            }
        );

        @SuppressWarnings("unchecked")
        protected final ContextualStreamCodec<ByteBuf, IScratchContextProvider<?>, ContextArgument<ENVIRONMENT, CONTEXT>> streamCodec = ContextualStreamCodec.<ByteBuf, IScratchContextProvider<?>, Integer>of(ByteBufCodecs.INT).map(
            (nesting, contextProvider) -> {
                for (int i = 0; i < nesting; i++) contextProvider = contextProvider.enclosingContextProvider();
                try {
                    return new ContextArgument<>(this, (IScratchContextProvider<CONTEXT>)contextProvider);
                } catch (ClassCastException e) {};
                throw new IllegalStateException("Could not find required context");
            }, (argument, contextProvider) -> {
                int nesting = 0;
                try {
                    while (contextProvider != argument.contextProvider && nesting < 255) {
                        contextProvider = contextProvider.enclosingContextProvider();
                        nesting++;
                    };
                    if (nesting >= 255) throw new IllegalStateException("Timed out trying to find context");
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("Could not find required context");
                };
                return nesting;
            }
        );

        public ContextParameter(String key) {
            this.key = key;
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextProvider<?>, ContextArgument<ENVIRONMENT, CONTEXT>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ContextArgument<ENVIRONMENT, CONTEXT>> argumentStreamCodec() {
            return streamCodec;
        };
        
    };
};
