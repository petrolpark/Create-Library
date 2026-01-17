package com.petrolpark.core.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.CompressorHolder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.KeyCompressor;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;

/**
 * Copy of {@link MapDecoder} that accepts a context object when decoding
 */
public interface ContextualMapDecoder<CONTEXT, A> extends Keyable {
    
    <T> DataResult<A> decode(DynamicOps<T> ops, CONTEXT context, MapLike<T> input);

    default <T> DataResult<A> compressedDecode(final DynamicOps<T> ops, final CONTEXT context, final T input) {
        if (ops.compressMaps()) {
            final Optional<Consumer<Consumer<T>>> inputList = ops.getList(input).result();

            if (!inputList.isPresent()) {
                return DataResult.error(() -> "Input is not a list");
            };

            final KeyCompressor<T> compressor = compressor(ops);
            final List<T> entries = new ArrayList<>();
            inputList.get().accept(entries::add);

            final MapLike<T> map = new MapLike<T>() {
                @Nullable
                @Override
                public T get(final T key) {
                    return entries.get(compressor.compress(key));
                }

                @Nullable
                @Override
                public T get(final String key) {
                    return entries.get(compressor.compress(key));
                }

                @Override
                public Stream<Pair<T, T>> entries() {
                    return IntStream.range(0, entries.size()).mapToObj(i -> Pair.of(compressor.decompress(i), entries.get(i))).filter(p -> p.getSecond() != null);
                }
            };
            return decode(ops, context, map);
        }
        // will use the lifecycle of decode
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, context, map));
    };

    <T> KeyCompressor<T> compressor(DynamicOps<T> ops);

    default ContextualDecoder<CONTEXT, A> decoder() {
        return new ContextualDecoder<CONTEXT, A>() {
            @Override
            public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final CONTEXT context, final T input) {
                return compressedDecode(ops, context, input).map(r -> Pair.of(r, input));
            };

            @Override
            public String toString() {
                return ContextualMapDecoder.this.toString();
            };
        };
    };

    default <B> ContextualMapDecoder<CONTEXT, B> map(final Function<? super A, ? extends B> function) {
        return new Implementation<CONTEXT, B>() {
            @Override
            public <T> DataResult<B> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
                return ContextualMapDecoder.this.decode(ops, context, input).map(function);
            };

            @Override
            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return ContextualMapDecoder.this.keys(ops);
            };

            @Override
            public String toString() {
                return ContextualMapDecoder.this.toString() + "[mapped]";
            };
        };
    };

    abstract class Implementation<CONTEXT, A> extends CompressorHolder implements ContextualMapDecoder<CONTEXT, A> {};

    static <CONTEXT, A> ContextualMapDecoder<CONTEXT, A> unit(final A instance) {
        return unit(() -> instance);
    };

    static <CONTEXT, A> ContextualMapDecoder<CONTEXT, A> unit(final Supplier<A> instance) {
        return new ContextualMapDecoder.Implementation<CONTEXT, A>() {
            @Override
            public <T> DataResult<A> decode(final DynamicOps<T> ops, CONTEXT context, final MapLike<T> input) {
                return DataResult.success(instance.get());
            };

            @Override
            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return Stream.empty();
            };

            @Override
            public String toString() {
                return "UnitDecoder[" + instance.get() + "]";
            };
        };
    };
};
