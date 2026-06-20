package com.petrolpark.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.util.codec.CodecHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.TriState;

/**
 * Extension to {@link Either} that permits <i>neither</i> value, in addition to just the left or just the right.
 */
public sealed interface Neither<L, R> permits Neither.Left, Neither.Right, Neither.None {

    public boolean isPresent();

    public boolean isLeft();

    public boolean isRight();

    public default boolean isEmpty() {
        return !isPresent();
    };

    public <C, D> Neither<C, D> mapBoth(final Function<? super L, ? extends C> f1, final Function<? super R, ? extends D> f2);

    public <T> Optional<T> map(final Function<? super L, ? extends T> l, Function<? super R, ? extends T> r);

    public Neither<L, R> ifLeft(final Consumer<? super L> consumer);

    public Neither<L, R> ifRight(final Consumer<? super R> consumer);

    public Neither<L, R> ifNeither(final Runnable runnable);

    public Optional<L> left();

    public Optional<R> right();

    public Optional<Either<L, R>> either();

    public default <T> Neither<T, R> mapLeft(final Function<? super L, ? extends T> l) {
        return mapBoth(l, Function.identity());
    };

    public default <T> Neither<L, T> mapRight(final Function<? super R, ? extends T> l) {
        return mapBoth(Function.identity(), l);
    };

    public static <L, R> MapCodec<Neither<L, R>> fieldCodec(final Codec<L> leftCodec, final Codec<R> rightCodec, final String key) {
        return Codec.either(leftCodec, rightCodec).optionalFieldOf(key).xmap(Neither::either, Neither::either);
    };

    public static <B extends ByteBuf, L, R> StreamCodec<B, Neither<L, R>> streamCodec(final StreamCodec<? super B, L> leftCodec, final StreamCodec<? super B, R> rightCodec) {
        return new NeitherStreamCodec<>(leftCodec, rightCodec);
    };

    public static <L, R> Neither<L, R> either(final Optional<Either<L, R>> either) {
        return either.map(Neither::either).orElseGet(Neither::neither);
    };

    public static <L, R> Neither<L, R> either(final Either<L, R> either) {
        return either.map(Neither::left, Neither::right);
    };

    public static <L, R> Neither<L, R> left(final L value) {
        return new Left<>(value);
    };

    public static <L, R> Neither<L, R> right(final R value) {
        return new Right<>(value);
    };

    public static <L, R> Neither<L, R> neither() {
        return new None<>();
    };

    public default Neither<R, L> swap() {
        return this.<Neither<R, L>>map(Neither::right, Neither::left).orElseGet(Neither::neither);
    };

    public default <L2> Neither<L2, R> flatMapLeft(final Function<L, Neither<L2, R>> function) {
        return this.<Neither<L2, R>>map(function, Neither::right).orElseGet(Neither::neither);
    };

    public final class Left<L, R> implements Neither<L, R> {

        protected final L value;

        public Left(L value) {
            this.value = value;
        };

        @Override
        public boolean isPresent() {
            return true;
        };

        @Override
        public boolean isLeft() {
            return true;
        };

        @Override
        public boolean isRight() {
            return false;
        };

        @Override
        public <C, D> Neither<C, D> mapBoth(Function<? super L, ? extends C> f1, Function<? super R, ? extends D> f2) {
            return new Left<>(f1.apply(value));
        };

        @Override
        public <T> Optional<T> map(Function<? super L, ? extends T> l, Function<? super R, ? extends T> r) {
            return Optional.of(l.apply(value));
        };

        @Override
        public Neither<L, R> ifLeft(Consumer<? super L> consumer) {
            consumer.accept(value);
            return this;
        };

        @Override
        public Neither<L, R> ifRight(Consumer<? super R> consumer) {
            return this;
        };

        @Override
        public Neither<L, R> ifNeither(Runnable runnable) {
            return this;
        };

        @Override
        public Optional<L> left() {
            return Optional.of(value);
        };

        @Override
        public Optional<R> right() {
            return Optional.empty();
        };

        @Override
        public Optional<Either<L, R>> either() {
            return Optional.of(Either.left(value));
        };

    };

    public final class Right<L, R> implements Neither<L, R> {

        protected final R value;
       
        public Right(R value) {
            this.value = value;
        };

        @Override
        public boolean isPresent() {
            return true;
        };

        @Override
        public boolean isLeft() {
            return false;
        };

        @Override
        public boolean isRight() {
            return true;
        };

        @Override
        public <C, D> Neither<C, D> mapBoth(Function<? super L, ? extends C> f1, Function<? super R, ? extends D> f2) {
            return new Right<>(f2.apply(value));
        };

        @Override
        public <T> Optional<T> map(Function<? super L, ? extends T> l, Function<? super R, ? extends T> r) {
            return Optional.of(r.apply(value));
        };

        @Override
        public Neither<L, R> ifLeft(Consumer<? super L> consumer) {
            return this;
        };

        @Override
        public Neither<L, R> ifRight(Consumer<? super R> consumer) {
            consumer.accept(value);
            return this;
        };

        @Override
        public Neither<L, R> ifNeither(Runnable runnable) {
            return this;
        };

        @Override
        public Optional<L> left() {
            return Optional.empty();
        };

        @Override
        public Optional<R> right() {
            return Optional.of(value);
        };

        @Override
        public Optional<Either<L, R>> either() {
            return Optional.of(Either.right(value));
        }
    };

    public final class None<L, R> implements Neither<L, R> {

        @Override
        public <C, D> Neither<C, D> mapBoth(Function<? super L, ? extends C> f1, Function<? super R, ? extends D> f2) {
            return new None<>();
        };

        @Override
        public boolean isPresent() {
            return false;
        };

        @Override
        public boolean isLeft() {
            return false;
        };

        @Override
        public boolean isRight() {
            return false;
        };

        @Override
        public <T> Optional<T> map(Function<? super L, ? extends T> l, Function<? super R, ? extends T> r) {
            return Optional.empty();
        };

        @Override
        public Neither<L, R> ifLeft(Consumer<? super L> consumer) {
            return this;
        };

        @Override
        public Neither<L, R> ifRight(Consumer<? super R> consumer) {
            return this;
        };

        @Override
        public Neither<L, R> ifNeither(Runnable runnable) {
            runnable.run();
            return this;
        };

        @Override
        public Optional<L> left() {
            return Optional.empty();
        };

        @Override
        public Optional<R> right() {
            return Optional.empty();
        };

        @Override
        public Optional<Either<L, R>> either() {
            return Optional.empty();
        };

    };

    public record NeitherStreamCodec<B extends ByteBuf, L, R>(StreamCodec<? super B, L> leftCodec, StreamCodec<? super B, R> rightCodec) implements StreamCodec<B, Neither<L, R>> {

        @Override
        public Neither<L, R> decode(@Nonnull B buffer) {
            final TriState state = CodecHelper.TRI_STATE_STREAM_CODEC.decode(buffer);
            if (state.isTrue()) return Neither.left(leftCodec.decode(buffer));
            if (state.isFalse()) return Neither.right(rightCodec.decode(buffer));
            return Neither.neither();
        };

        @Override
        public void encode(@Nonnull B buffer, @Nonnull Neither<L, R> value) {
            CodecHelper.TRI_STATE_STREAM_CODEC.encode(buffer, value.isLeft() ? TriState.TRUE : value.isRight() ? TriState.FALSE : TriState.DEFAULT);
            value.ifLeft(l -> leftCodec.encode(buffer, l));
            value.ifRight(r -> rightCodec.encode(buffer, r));
        };

    };
};
