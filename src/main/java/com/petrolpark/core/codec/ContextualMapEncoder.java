package com.petrolpark.core.codec;

import java.util.stream.Stream;

import com.mojang.serialization.CompressorHolder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.KeyCompressor;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.RecordBuilder;

/**
 * Copy of {@link MapDecoder} that accepts a context object when encoding
 */
public interface ContextualMapEncoder<CONTEXT, A> extends Keyable {
    
    <T> RecordBuilder<T> encode(A input, CONTEXT context, DynamicOps<T> ops, RecordBuilder<T> prefix);

    default <T> RecordBuilder<T> compressedBuilder(final DynamicOps<T> ops) {
        if (ops.compressMaps()) {
            return MapEncoder.makeCompressedBuilder(ops, compressor(ops));
        }
        return ops.mapBuilder();
    }

    <T> KeyCompressor<T> compressor(final DynamicOps<T> ops);

    abstract class Implementation<CONTEXT, A> extends CompressorHolder implements ContextualMapEncoder<CONTEXT, A> {
    }

    static <CONTEXT, A> ContextualMapEncoder<CONTEXT, A> empty() {
        return new ContextualMapEncoder.Implementation<CONTEXT, A>() {
            @Override
            public <T> RecordBuilder<T> encode(final A input, CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                return prefix;
            };

            @Override
            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return Stream.empty();
            };

            @Override
            public String toString() {
                return "EmptyEncoder";
            };
        };
    }
};
