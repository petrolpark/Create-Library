package com.petrolpark.core.codec;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Copy of {@link Codec} that accepts a context object when encoding and decoding
 */
public interface ContextualCodec<CONTEXT, A> extends ContextualEncoder<CONTEXT, A>, ContextualDecoder<CONTEXT, A> {

    public static <CONTEXT, A> ContextualCodec<CONTEXT, A> of(Codec<A> codec) {
        return new ContextualCodec<>() {

            @Override
            public <T> DataResult<T> encode(A input, CONTEXT context, DynamicOps<T> ops, T prefix) {
                return codec.encode(input, ops, prefix);
            };

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, CONTEXT context, T input) {
                return codec.decode(ops, input);
            };

            @Override
            public String toString() {
                return "Wrapped[" + codec + "]";
            };

        };
    };

    public static <CONTEXT, A> ContextualCodec<CONTEXT, A> of(final ContextualEncoder<CONTEXT, A> encoder, final ContextualDecoder<CONTEXT, A> decoder, final String name) {
        return new ContextualCodec<CONTEXT, A>() {

            @Override
            public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final CONTEXT context, final T input) {
                return decoder.decode(ops, context, input);
            };

            @Override
            public <T> DataResult<T> encode(final A input, final CONTEXT context, final DynamicOps<T> ops, final T prefix) {
                return encoder.encode(input, context, ops, prefix);
            };

            @Override
            public String toString() {
                return name;
            };
        };
    };

    public static <CONTEXT, A> ContextualCodec<CONTEXT, A> unit(final A defaultValue) {
        return unit(() -> defaultValue);
    };

    public static <CONTEXT, A> ContextualCodec<CONTEXT, A> unit(final Supplier<A> defaultValue) {
        return ContextualMapCodec.<CONTEXT, A>unit(defaultValue).codec();
    };

    public static <CONTEXT, F> ContextualMapCodec<CONTEXT, Optional<F>> optionalField(final String name, final ContextualCodec<CONTEXT, F> elementCodec, final boolean lenient) {
        return new ContextualOptionalFieldCodec<>(name, elementCodec, lenient);
    };

    public default <S> ContextualCodec<CONTEXT, S> xmap(final Function<? super A, ? extends S> to, final Function<? super S, ? extends A> from) {
        return ContextualCodec.of(comap(from), map(to), toString() + "[xmapped]");
    };

    public default <S> ContextualCodec<CONTEXT, S> flatContextualXmap(final BiFunction<CONTEXT, ? super A, ? extends DataResult<? extends S>> to, final BiFunction<CONTEXT, ? super S, ? extends DataResult<? extends A>> from) {
        return ContextualCodec.of(flatContextualComap(from), flatContextualMap(to), toString() + "[flatXmapped]");
    };

    public static <CONTEXT, A> ContextualCodec<CONTEXT, A> withContext(final ContextualCodec<CONTEXT, A> codec, final CONTEXT newContext) {
        return new ContextualCodec<>() {

            @Override
            public <T> DataResult<T> encode(A input, CONTEXT context, DynamicOps<T> ops, T prefix) {
                return codec.encode(input, newContext, ops, prefix);
            };

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, CONTEXT context, T input) {
                return codec.decode(ops, newContext, input);
            };
        };
    };

    public default ContextualCodec<CONTEXT, A> withContext(final CONTEXT newContext) {
        return withContext(this, newContext);
    };

    @Override
    public default ContextualMapCodec<CONTEXT, A> fieldOf(final String name) {
        return ContextualMapCodec.of(
            ContextualEncoder.super.fieldOf(name),
            ContextualDecoder.super.fieldOf(name),
            () -> "Field[" + name + ": " + toString() + "]"
        );
    };

    public default ContextualMapCodec<CONTEXT, Optional<A>> optionalFieldOf(final String name) {
        return optionalField(name, this, false);
    };

};
