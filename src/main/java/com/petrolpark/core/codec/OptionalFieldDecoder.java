package com.petrolpark.core.codec;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;

public class OptionalFieldDecoder<A> extends MapDecoder.Implementation<Optional<A>> {
    private final String name;
    private final Decoder<A> elementDecoder;
    private final boolean lenient;

    public OptionalFieldDecoder(final String name, final Decoder<A> elementDecoder, final boolean lenient) {
        this.name = name;
        this.elementDecoder = elementDecoder;
        this.lenient = lenient;
    };

    @Override
    public <T> DataResult<Optional<A>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final T value = input.get(name);
        if (value == null) return DataResult.success(Optional.empty());
        final DataResult<A> parsed = elementDecoder.parse(ops, value);
        if (parsed.isError() && lenient) return DataResult.success(Optional.empty());
        return parsed.map(Optional::of).setPartial(parsed.resultOrPartial());
    };

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return Stream.of(ops.createString(name));
    };

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OptionalFieldDecoder<?> that = (OptionalFieldDecoder<?>) o;
        return Objects.equals(name, that.name) && Objects.equals(elementDecoder, that.elementDecoder) && lenient == that.lenient;
    };

    @Override
    public int hashCode() {
        return Objects.hash(name, elementDecoder, lenient);
    };

    @Override
    public String toString() {
        return "OptionalFieldDecoder[" + name + ": " + elementDecoder + ']';
    };
};
