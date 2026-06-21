package petrolpark.mc.library.util.codec;

import java.util.Objects;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.FieldDecoder;

/**
 * Copy of {@link FieldDecoder} that accepts a context object when decoding
 */
public final class ContextualFieldDecoder<CONTEXT, A> extends ContextualMapDecoder.Implementation<CONTEXT, A> {
    
    protected final String name;
    private final ContextualDecoder<CONTEXT, A> elementCodec;

    public ContextualFieldDecoder(final String name, final ContextualDecoder<CONTEXT, A> elementCodec) {
        this.name = name;
        this.elementCodec = elementCodec;
    };

    @Override
    public <T> DataResult<A> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
        final T value = input.get(name);
        if (value == null) return DataResult.error(() -> "No key " + name + " in " + input);
        return elementCodec.parse(ops, context, value);
    };

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return Stream.of(ops.createString(name));
    };

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ContextualFieldDecoder<?, ?> that = (ContextualFieldDecoder<?, ?>) o;
        return Objects.equals(name, that.name) && Objects.equals(elementCodec, that.elementCodec);
    };

    @Override
    public int hashCode() {
        return Objects.hash(name, elementCodec);
    };

    @Override
    public String toString() {
        return "FieldDecoder[" + name + ": " + elementCodec + ']';
    };
    
};
