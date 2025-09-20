package com.petrolpark.core.codec;

import java.util.function.Function;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;

/**
 * Copy of {@link Encoder} that accepts a context object when encoding and decoding
 */
public interface ContextualEncoder<CONTEXT, A> {
    
    <T> DataResult<T> encode(final A input, final CONTEXT context, final DynamicOps<T> ops, final T prefix);

    public default <T> DataResult<T> encodeStart(final DynamicOps<T> ops, final CONTEXT context, final A input) {
        return encode(input, context, ops, ops.empty());
    };

    public default ContextualMapEncoder<CONTEXT, A> fieldOf(final String name) {
        return new ContextualFieldEncoder<>(name, this);
    };

    public default <B> ContextualEncoder<CONTEXT, B> comap(final Function<? super B, ? extends A> function) {
        return new ContextualEncoder<CONTEXT, B>() {

            @Override
            public <T> DataResult<T> encode(final B input, final CONTEXT context, final DynamicOps<T> ops, final T prefix) {
                return ContextualEncoder.this.encode(function.apply(input), context, ops, prefix);
            };

            @Override
            public String toString() {
                return ContextualEncoder.this.toString() + "[comapped]";
            };
        };
    }
};
