package com.petrolpark.core.codec;

import java.util.Objects;
import java.util.stream.Stream;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.FieldEncoder;

/**
 * Copy of {@link FieldEncoder} that accepts a context object when encoding
 */
public class ContextualFieldEncoder<CONTEXT, A> extends ContextualMapEncoder.Implementation<CONTEXT, A> {
    private final String name;
    private final ContextualEncoder<CONTEXT, A> elementCodec;

    public ContextualFieldEncoder(final String name, final ContextualEncoder<CONTEXT, A> elementCodec) {
        this.name = name;
        this.elementCodec = elementCodec;
    };

    @Override
    public <T> RecordBuilder<T> encode(final A input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        return prefix.add(name, elementCodec.encodeStart(ops, context, input));
    };

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return Stream.of(ops.createString(name));
    };

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ContextualFieldEncoder<?, ?> that = (ContextualFieldEncoder<?, ?>) o;
        return Objects.equals(name, that.name) && Objects.equals(elementCodec, that.elementCodec);
    };

    @Override
    public int hashCode() {
        return Objects.hash(name, elementCodec);
    };

    @Override
    public String toString() {
        return "FieldEncoder[" + name + ": " + elementCodec + ']';
    };
};
