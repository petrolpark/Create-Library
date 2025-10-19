package com.petrolpark.core.codec;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.CompressorHolder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

/**
 * Copy of {@link MapCodec} that accepts a context object when encoding and decoding
 */
public abstract class ContextualMapCodec<CONTEXT, A> extends CompressorHolder implements ContextualMapEncoder<CONTEXT, A>, ContextualMapDecoder<CONTEXT, A> {

    public final <O> RecordContextualCodecBuilder<CONTEXT, O, A> forGetter(final BiFunction<CONTEXT, O, A> getter) {
        return RecordContextualCodecBuilder.of(getter, this);
    };

    public final <O> RecordContextualCodecBuilder<CONTEXT, O, A> forGetter(final Function<O, A> getter) {
        return forGetter((c, o) -> getter.apply(o));
    };

    public static <CONTEXT, A> ContextualMapCodec<CONTEXT, A> of(final ContextualMapEncoder<CONTEXT, A> encoder, final ContextualMapDecoder<CONTEXT, A> decoder) {
        return of(encoder, decoder, () -> "MapCodec[" + encoder + " " + decoder + "]");
    };

    public static <CONTEXT, A> ContextualMapCodec<CONTEXT, A> of(final ContextualMapEncoder<CONTEXT, A> encoder, final ContextualMapDecoder<CONTEXT, A> decoder, final Supplier<String> name) {
        return new ContextualMapCodec<CONTEXT, A>() {
            @Override
            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return Stream.concat(encoder.keys(ops), decoder.keys(ops));
            };

            @Override
            public <T> DataResult<A> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
                return decoder.decode(ops, context, input);
            };

            @Override
            public <T> RecordBuilder<T> encode(final A input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                return encoder.encode(input, context, ops, prefix);
            };

            @Override
            public String toString() {
                return name.get();
            };
        };
    }
    
    public record ContextualMapCodecCodec<CONTEXT, A>(ContextualMapCodec<CONTEXT, A> codec) implements ContextualCodec<CONTEXT, A> {
        @Override
        public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final CONTEXT context, final T input) {
            return codec.compressedDecode(ops, context, input).map(r -> Pair.of(r, input));
        };

        @Override
        public <T> DataResult<T> encode(final A input, final CONTEXT context, final DynamicOps<T> ops, final T prefix) {
            return codec.encode(input, context, ops, codec.compressedBuilder(ops)).build(prefix);
        };

        @Override
        public String toString() {
            return codec.toString();
        };
    };

    public ContextualCodec<CONTEXT, A> codec() {
        return new ContextualMapCodecCodec<>(this);
    };

    public static <CONTEXT, A> ContextualMapCodec<CONTEXT, A> unit(final A defaultValue) {
        return unit(() -> defaultValue);
    }

    public static <CONTEXT, A> ContextualMapCodec<CONTEXT, A> unit(final Supplier<A> defaultValue) {
        return of(ContextualMapEncoder.empty(), ContextualMapDecoder.unit(defaultValue));
    };
};
