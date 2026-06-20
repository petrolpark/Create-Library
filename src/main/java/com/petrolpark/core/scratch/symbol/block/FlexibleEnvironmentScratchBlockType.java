package com.petrolpark.core.scratch.symbol.block;

import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;
import com.petrolpark.util.codec.ContextualMapCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;

import io.netty.buffer.ByteBuf;

public class FlexibleEnvironmentScratchBlockType<BASE_ENVIRONMENT extends IScratchEnvironment, BLOCK extends IScratchBlock<?, ?, ?>> implements IScratchBlock.Type<BLOCK> {

    protected final Function<IScratchEnvironment.Type<?>, BLOCK> factory;

    protected final ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec;
    protected final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec;

    public FlexibleEnvironmentScratchBlockType(Class<BASE_ENVIRONMENT> environmentClass, Function<IScratchEnvironment.Type<?>, BLOCK> factory) {
        this.factory = factory;

        codec = new ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK>() {

            @Override
            public <T> RecordBuilder<T> encode(BLOCK input, IScratchEnvironment.Type<?> context, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix;
            };

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            };

            @Override
            public <T> DataResult<BLOCK> decode(DynamicOps<T> ops, IScratchEnvironment.Type<?> context, MapLike<T> input) {
                return decodeInternal(context);
            };

            @SuppressWarnings("unchecked")
            protected <ACTUAL_ENVIRONMENT extends IScratchEnvironment, T> DataResult<BLOCK> decodeInternal(IScratchEnvironment.Type<ACTUAL_ENVIRONMENT> context) {
                if (environmentClass.isAssignableFrom(context.getClass())) {
                    final BLOCK block = factory.apply(context);
                    if (context.allows((IScratchSymbol<? super ACTUAL_ENVIRONMENT, ?, ?>)block)) {
                        return DataResult.success(block);
                    } else {
                        return DataResult.error(() -> "Block is not permitted");
                    }
                } else {
                    return DataResult.error(() -> "Block cannot be assigned to this Environment");
                }
            };
            
        };

        streamCodec = ContextualStreamCodec.of(factory);
    };

    @Override
    public ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec() {
        return codec;
    };

    @Override
    public ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec() {
        return streamCodec;
    };
    
};
