package com.petrolpark.util;

import java.util.function.Supplier;

public class FunctionHelper {
    
    public static final <T> Supplier<T> memoizeNonNull(Supplier<T> delegate) {

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

    public static final <T> Supplier<T> withFallback(Supplier<T> delegate, Supplier<T> fallback) {
        return () -> {
            T value = delegate.get();
            return value == null ? fallback.get() : value;
        };
    };
};
