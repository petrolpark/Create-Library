package com.petrolpark.util;

import java.util.Optional;

import javax.annotation.Nullable;

public class Holder<T> {
    
    private T value = null;

    public static final <T> Holder<T> of(T value) {
        return new Holder<>(value);
    };

    public static final <T> Holder<T> empty() {
        return new Holder<>(null);
    };

    protected Holder(T value) {
        set(value);
    };

    public boolean isEmpty() {
        return value == null;
    };

    @Nullable
    public T get() {
        return value;
    };

    public Optional<T> getOptional() {
        return Optional.ofNullable(value);
    };

    public void set(T value) {
        this.value = value;
    };
};
