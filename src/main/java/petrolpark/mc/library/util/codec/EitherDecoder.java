package petrolpark.mc.library.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;

public record EitherDecoder<F, S>(Decoder<F> first, Decoder<S> second) implements Decoder<Either<F, S>> {

    @Override
    public <T> DataResult<Pair<Either<F, S>, T>> decode(final DynamicOps<T> ops, final T input) {
        final DataResult<Pair<Either<F, S>, T>> firstRead = first.decode(ops, input).map(vo -> vo.mapFirst(Either::left));
        if (firstRead.isSuccess()) return firstRead;
        
        final DataResult<Pair<Either<F, S>, T>> secondRead = second.decode(ops, input).map(vo -> vo.mapFirst(Either::right));
        if (secondRead.isSuccess()) return secondRead;
        
        if (firstRead.hasResultOrPartial()) return firstRead;
        
        if (secondRead.hasResultOrPartial()) return secondRead;
        
        return DataResult.error(() -> "Failed to parse either. First: " + firstRead.error().orElseThrow().message() + "; Second: " + secondRead.error().orElseThrow().message());
    };

    public static <T> Decoder<T> withAlternative(final Decoder<T> primary, final Decoder<? extends T> alternative) {
        return new EitherDecoder<>(primary, alternative).map(Either::unwrap);
    };

};
