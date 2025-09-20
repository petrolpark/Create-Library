package com.petrolpark.core.codec;

import java.util.function.Function;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

/**
 * Copy of {@link Decoder} that accepts a context object when decoding
 */
public interface ContextualDecoder<CONTEXT, A> {
    
    <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final CONTEXT context, final T input);

    public default <T> DataResult<A> parse(final DynamicOps<T> ops, final CONTEXT context, final T input) {
        return decode(ops, context, input).map(Pair::getFirst);
    };

    public default <T> DataResult<Pair<A, T>> decode(final CONTEXT context, final Dynamic<T> input) {
        return decode(input.getOps(), context, input.getValue());
    };

    public default <T> DataResult<A> parse(final CONTEXT context, final Dynamic<T> input) {
        return decode(context, input).map(Pair::getFirst);
    };

    public default ContextualMapDecoder<CONTEXT, A> fieldOf(final String name) {
        return new ContextualFieldDecoder<>(name, this);
    };

    public default <B> ContextualDecoder<CONTEXT, B> map(final Function<? super A, ? extends B> function) {
        return new ContextualDecoder<CONTEXT, B>() {

            @Override
            public <T> DataResult<Pair<B, T>> decode(final DynamicOps<T> ops, final CONTEXT context, final T input) {
                return ContextualDecoder.this.decode(ops, context, input).map(p -> p.mapFirst(function));
            };

            @Override
            public String toString() {
                return ContextualDecoder.this.toString() + "[mapped]";
            };
        };
    }
};
