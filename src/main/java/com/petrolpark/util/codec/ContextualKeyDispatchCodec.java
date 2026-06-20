package com.petrolpark.util.codec;

import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.KeyDispatchCodec;

/**
 * Copy of {@link KeyDispatchCodec} that accepts a context object. The Key Codec does not include the context, but the value Codec does.
 */
public class ContextualKeyDispatchCodec<CONTEXT, K, V> extends ContextualMapCodec<CONTEXT, V> {

    private static final String COMPRESSED_VALUE_KEY = "value";
    private final String typeKey;
    private final Codec<K> keyCodec;
    private final Function<? super V, ? extends DataResult<? extends K>> type;
    private final Function<? super K, ? extends DataResult<? extends ContextualMapDecoder<CONTEXT, ? extends V>>> decoder;
    private final Function<? super V, ? extends DataResult<? extends ContextualMapEncoder<CONTEXT, V>>> encoder;

    protected ContextualKeyDispatchCodec(final String typeKey, final Codec<K> keyCodec, final Function<? super V, ? extends DataResult<? extends K>> type, final Function<? super K, ? extends DataResult<? extends ContextualMapDecoder<CONTEXT, ? extends V>>> decoder, final Function<? super V, ? extends DataResult<? extends ContextualMapEncoder<CONTEXT, V>>> encoder) {
        this.typeKey = typeKey;
        this.keyCodec = keyCodec;
        this.type = type;
        this.decoder = decoder;
        this.encoder = encoder;
    };

    public ContextualKeyDispatchCodec(final String typeKey, final Codec<K> keyCodec, final Function<? super V, ? extends DataResult<? extends K>> type, final Function<? super K, ? extends DataResult<? extends ContextualMapCodec<CONTEXT, ? extends V>>> codec) {
        this(typeKey, keyCodec, type, codec, v -> getCodec(type, codec, v));
    };

    @Override
    public <T> DataResult<V> decode(final DynamicOps<T> ops, CONTEXT context, final MapLike<T> input) {
        final T elementName = input.get(typeKey);
        if (elementName == null) return DataResult.error(() -> "Input does not contain a key [" + typeKey + "]: " + input);

        return keyCodec.decode(ops, elementName).flatMap(type ->
            decoder.apply(type.getFirst()).flatMap(elementDecoder -> {
                if (ops.compressMaps()) {
                    final T value = input.get(ops.createString(COMPRESSED_VALUE_KEY));
                    if (value == null) {
                        return DataResult.error(() -> "Input does not have a \"value\" entry: " + input);
                    }
                    return elementDecoder.decoder().parse(ops, context, value).map(Function.identity());
                }
                return elementDecoder.decode(ops, context, input).map(Function.identity());
            })
        );
    };

    @Override
    public <T> RecordBuilder<T> encode(final V input, CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        final DataResult<? extends ContextualMapEncoder<CONTEXT, V>> encoderResult = encoder.apply(input);
        final RecordBuilder<T> builder = prefix.withErrorsFrom(encoderResult);
        if (encoderResult.isError()) return builder;

        final ContextualMapEncoder<CONTEXT, V> elementEncoder = encoderResult.result().get();
        if (ops.compressMaps()) {
            return prefix
                .add(typeKey, type.apply(input).flatMap(t -> keyCodec.encodeStart(ops, t)))
                .add(COMPRESSED_VALUE_KEY, elementEncoder.encoder().encodeStart(ops, context, input));
        };

        return elementEncoder.encode(input, context, ops, prefix).add(typeKey, type.apply(input).flatMap(t -> keyCodec.encodeStart(ops, t)));
    };

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return Stream.of(typeKey, COMPRESSED_VALUE_KEY).map(ops::createString);
    };

    @SuppressWarnings("unchecked")
    private static <CONTEXT, K, V> DataResult<? extends ContextualMapEncoder<CONTEXT, V>> getCodec(final Function<? super V, ? extends DataResult<? extends K>> type, final Function<? super K, ? extends DataResult<? extends ContextualMapEncoder<CONTEXT, ? extends V>>> encoder, final V input) {
        return type.apply(input)
            .<ContextualMapEncoder<CONTEXT, ? extends V>>flatMap(key -> encoder.apply(key).map(Function.identity()))
            .map(c -> ((ContextualMapEncoder<CONTEXT, V>) c));
    };

    @Override
    public String toString() {
        return "ContextualKeyDispatchCodec[" + keyCodec.toString() + " " + type + " " + decoder + "]";
    };
};
