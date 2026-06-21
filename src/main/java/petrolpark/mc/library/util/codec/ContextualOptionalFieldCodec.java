package petrolpark.mc.library.util.codec;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

public final class ContextualOptionalFieldCodec<CONTEXT, A> extends ContextualMapCodec<CONTEXT, Optional<A>> {
    
    private final String name;
    private final ContextualCodec<CONTEXT, A> elementCodec;
    private final boolean lenient;

    public ContextualOptionalFieldCodec(final String name, final ContextualCodec<CONTEXT, A> elementCodec, final boolean lenient) {
        this.name = name;
        this.elementCodec = elementCodec;
        this.lenient = lenient;
    };

    @Override
    public <T> DataResult<Optional<A>> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
        final T value = input.get(name);
        if (value == null) return DataResult.success(Optional.empty());
        final DataResult<A> parsed = elementCodec.parse(ops, context, value);
        if (parsed.isError() && lenient) return DataResult.success(Optional.empty());
        return parsed.map(Optional::of).setPartial(parsed.resultOrPartial());
    };

    @Override
    public <T> RecordBuilder<T> encode(final Optional<A> input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        if (input.isPresent()) return prefix.add(name, elementCodec.encodeStart(ops, context, input.get()));
        return prefix;
    };

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return Stream.of(ops.createString(name));
    };

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ContextualOptionalFieldCodec<?, ?> that = (ContextualOptionalFieldCodec<?, ?>) o;
        return Objects.equals(name, that.name) && Objects.equals(elementCodec, that.elementCodec) && lenient == that.lenient;
    };

    @Override
    public int hashCode() {
        return Objects.hash(name, elementCodec, lenient);
    };

    @Override
    public String toString() {
        return "OptionalFieldCodec[" + name + ": " + elementCodec + ']';
    };
};
