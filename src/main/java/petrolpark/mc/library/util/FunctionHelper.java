package petrolpark.mc.library.util;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionHelper {
    
    public static final <T> Supplier<T> memoizeNonNull(final Supplier<T> delegate) {

        return new Supplier<>() {
            private volatile T value;

            @Override
            public T get() {
                T result = value;
                if (result == null) {
                    synchronized (this) {
                        result = value;
                        if (result == null) {
                            result = delegate.get();
                            if (result != null) {
                                value = result;
                            };
                        };
                    };
                };
                return result;
            }
        };
    };

    public static final <T> Supplier<T> withFallback(final Supplier<? extends T> delegate, final Supplier<? extends T> fallback) {
        return () -> {
            final T value = delegate.get();
            return value == null ? fallback.get() : value;
        };
    };

    public static final <T, R> Function<T, R> withFallback(final Function<? super T, ? extends R> primary, final Function<? super T, ? extends R> fallback) {
        return x -> {
            final R result = primary.apply(x);
            return result == null ? fallback.apply(x) : result;
        };
    };

    public static final <S, T> BiPredicate<S, T> not(BiPredicate<S, T> predicate) {
        return predicate.negate();
    };
};
