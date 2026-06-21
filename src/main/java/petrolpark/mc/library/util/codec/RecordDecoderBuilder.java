package petrolpark.mc.library.util.codec;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Copy of {@link RecordCodecBuilder} for {@link Decoder}s only
 */
public final class RecordDecoderBuilder<O, F> implements App<RecordDecoderBuilder.Mu<O>, F> {

    public static final class Mu<O> implements K1 {}

    public static <O, F> RecordDecoderBuilder<O, F> unbox(final App<Mu<O>, F> box) {
        return ((RecordDecoderBuilder<O, F>) box);
    };

    private final MapDecoder<F> decoder;

    private RecordDecoderBuilder(final MapDecoder<F> decoder) {
        this.decoder = decoder;
    };

    public static <O> Instance<O> instance() {
        return new Instance<>();
    };

    public static <O, F> RecordDecoderBuilder<O, F> of(final String name, final Codec<F> fieldCodec) {
        return of(fieldCodec.fieldOf(name));
    };

    public static <O, F> RecordDecoderBuilder<O, F> of(final MapDecoder<F> decoder) {
        return new RecordDecoderBuilder<>(decoder);
    };

    public static <O, F> RecordDecoderBuilder<O, Optional<F>> ofOptional(final Decoder<F> elementDecoder, final String name) {
        return ofOptional(elementDecoder, name, false);
    };

    public static <O, F> RecordDecoderBuilder<O, Optional<F>> ofOptional(final Decoder<F> elementDecoder, final String name, final boolean lenient) {
        return of(new OptionalFieldDecoder<>(name, elementDecoder, lenient));
    };

    public static <O, F> RecordDecoderBuilder<O, F> ofOptional(final Decoder<F> elementDecoder, final String name, final F defaultValue) {
        return ofOptional(elementDecoder, name, defaultValue, false);
    };

    public static <O, F> RecordDecoderBuilder<O, F> ofOptional(final Decoder<F> elementDecoder, final String name, final F defaultValue, final boolean lenient) {
        return of(new OptionalFieldDecoder<>(name, elementDecoder, lenient).map(optional -> optional.orElse(defaultValue)));
    };

    public static <O, F> RecordDecoderBuilder<O, F> point(final F instance) {
        return new RecordDecoderBuilder<>(Decoder.unit(instance));
    };

    public static <O, F> RecordDecoderBuilder<O, F> stable(final F instance) {
        return point(instance, Lifecycle.stable());
    };

    public static <O, F> RecordDecoderBuilder<O, F> deprecated(final F instance, final int since) {
        return point(instance, Lifecycle.deprecated(since));
    };

    public static <O, F> RecordDecoderBuilder<O, F> point(final F instance, final Lifecycle lifecycle) {
        return new RecordDecoderBuilder<>(Decoder.unit(instance).withLifecycle(lifecycle));
    };

    public static <O> Decoder<O> create(final Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance())).decoder();
    };

    public static <O> MapDecoder<O> mapDecoder(final Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance()));
    };

    public static <O> MapDecoder<O> build(final App<Mu<O>, O> builderBox) {
        final RecordDecoderBuilder<O, O> builder = unbox(builderBox);
        return new MapDecoder.Implementation<O>() {
            @Override
            public <T> DataResult<O> decode(final DynamicOps<T> ops, final MapLike<T> input) {
                return builder.decoder.decode(ops, input);
            };

            @Override
            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return builder.decoder.keys(ops);
            };

            @Override
            public String toString() {
                return "RecordCodec[" + builder.decoder + "]";
            };
        };
    };

    public static final class Instance<O> implements Applicative<Mu<O>, Instance.Mu<O>> {
        private static final class Mu<O> implements Applicative.Mu {}

        public <A> App<RecordDecoderBuilder.Mu<O>, A> stable(final A a) {
            return RecordDecoderBuilder.stable(a);
        };

        public <A> App<RecordDecoderBuilder.Mu<O>, A> deprecated(final A a, final int since) {
            return RecordDecoderBuilder.deprecated(a, since);
        };

        public <A> App<RecordDecoderBuilder.Mu<O>, A> point(final A a, final Lifecycle lifecycle) {
            return RecordDecoderBuilder.point(a, lifecycle);
        };

        @Override
        public <A> App<RecordDecoderBuilder.Mu<O>, A> point(final A a) {
            return RecordDecoderBuilder.point(a);
        }
;
        @Override
        public <A, R> Function<App<RecordDecoderBuilder.Mu<O>, A>, App<RecordDecoderBuilder.Mu<O>, R>> lift1(final App<RecordDecoderBuilder.Mu<O>, Function<A, R>> function) {
            return fa -> {
                final RecordDecoderBuilder<O, Function<A, R>> f = unbox(function);
                final RecordDecoderBuilder<O, A> a = unbox(fa);

                return new RecordDecoderBuilder<>(

                    new MapDecoder.Implementation<R>() {
                        @Override
                        public <T> DataResult<R> decode(final DynamicOps<T> ops, final MapLike<T> input) {
                            return a.decoder.decode(ops, input).flatMap(ar ->
                                f.decoder.decode(ops, input).map(fr ->
                                    fr.apply(ar)
                                )
                            );
                        }

                        @Override
                        public <T> Stream<T> keys(final DynamicOps<T> ops) {
                            return Stream.concat(a.decoder.keys(ops), f.decoder.keys(ops));
                        }

                        @Override
                        public String toString() {
                            return f.decoder + " * " + a.decoder;
                        }
                    }
                );
            };
        }

        @Override
        public <A, B, R> App<RecordDecoderBuilder.Mu<O>, R> ap2(final App<RecordDecoderBuilder.Mu<O>, BiFunction<A, B, R>> func, final App<RecordDecoderBuilder.Mu<O>, A> a, final App<RecordDecoderBuilder.Mu<O>, B> b) {
            final RecordDecoderBuilder<O, BiFunction<A, B, R>> function = unbox(func);
            final RecordDecoderBuilder<O, A> fa = unbox(a);
            final RecordDecoderBuilder<O, B> fb = unbox(b);

            return new RecordDecoderBuilder<>(
                new MapDecoder.Implementation<R>() {
                    @Override
                    public <T> DataResult<R> decode(final DynamicOps<T> ops, final MapLike<T> input) {
                        return DataResult.unbox(DataResult.instance().ap2(
                            function.decoder.decode(ops, input),
                            fa.decoder.decode(ops, input),
                            fb.decoder.decode(ops, input)
                        ));
                    };

                    @Override
                    public <T> Stream<T> keys(final DynamicOps<T> ops) {
                        return Stream.of(
                            function.decoder.keys(ops),
                            fa.decoder.keys(ops),
                            fb.decoder.keys(ops)
                        ).flatMap(Function.identity());
                    };

                    @Override
                    public String toString() {
                        return function.decoder + " * " + fa.decoder + " * " + fb.decoder;
                    };
                }
            );
        };

        @Override
        public <T1, T2, T3, R> App<RecordDecoderBuilder.Mu<O>, R> ap3(final App<RecordDecoderBuilder.Mu<O>, Function3<T1, T2, T3, R>> func, final App<RecordDecoderBuilder.Mu<O>, T1> t1, final App<RecordDecoderBuilder.Mu<O>, T2> t2, final App<RecordDecoderBuilder.Mu<O>, T3> t3) {
            final RecordDecoderBuilder<O, Function3<T1, T2, T3, R>> function = unbox(func);
            final RecordDecoderBuilder<O, T1> f1 = unbox(t1);
            final RecordDecoderBuilder<O, T2> f2 = unbox(t2);
            final RecordDecoderBuilder<O, T3> f3 = unbox(t3);

            return new RecordDecoderBuilder<>(
                new MapDecoder.Implementation<R>() {
                    @Override
                    public <T> DataResult<R> decode(final DynamicOps<T> ops, final MapLike<T> input) {
                        return DataResult.unbox(DataResult.instance().ap3(
                            function.decoder.decode(ops, input),
                            f1.decoder.decode(ops, input),
                            f2.decoder.decode(ops, input),
                            f3.decoder.decode(ops, input)
                        ));
                    }

                    @Override
                    public <T> Stream<T> keys(final DynamicOps<T> ops) {
                        return Stream.of(
                            function.decoder.keys(ops),
                            f1.decoder.keys(ops),
                            f2.decoder.keys(ops),
                            f3.decoder.keys(ops)
                        ).flatMap(Function.identity());
                    }

                    @Override
                    public String toString() {
                        return function.decoder + " * " + f1.decoder + " * " + f2.decoder + " * " + f3.decoder;
                    };
                }
            );
        };

        @Override
        public <T1, T2, T3, T4, R> App<RecordDecoderBuilder.Mu<O>, R> ap4(final App<RecordDecoderBuilder.Mu<O>, Function4<T1, T2, T3, T4, R>> func, final App<RecordDecoderBuilder.Mu<O>, T1> t1, final App<RecordDecoderBuilder.Mu<O>, T2> t2, final App<RecordDecoderBuilder.Mu<O>, T3> t3, final App<RecordDecoderBuilder.Mu<O>, T4> t4) {
            final RecordDecoderBuilder<O, Function4<T1, T2, T3, T4, R>> function = unbox(func);
            final RecordDecoderBuilder<O, T1> f1 = unbox(t1);
            final RecordDecoderBuilder<O, T2> f2 = unbox(t2);
            final RecordDecoderBuilder<O, T3> f3 = unbox(t3);
            final RecordDecoderBuilder<O, T4> f4 = unbox(t4);

            return new RecordDecoderBuilder<>(
                new MapDecoder.Implementation<R>() {
                    @Override
                    public <T> DataResult<R> decode(final DynamicOps<T> ops, final MapLike<T> input) {
                        return DataResult.unbox(DataResult.instance().ap4(
                            function.decoder.decode(ops, input),
                            f1.decoder.decode(ops, input),
                            f2.decoder.decode(ops, input),
                            f3.decoder.decode(ops, input),
                            f4.decoder.decode(ops, input)
                        ));
                    }

                    @Override
                    public <T> Stream<T> keys(final DynamicOps<T> ops) {
                        return Stream.of(
                            function.decoder.keys(ops),
                            f1.decoder.keys(ops),
                            f2.decoder.keys(ops),
                            f3.decoder.keys(ops),
                            f4.decoder.keys(ops)
                        ).flatMap(Function.identity());
                    };

                    @Override
                    public String toString() {
                        return function.decoder + " * " + f1.decoder + " * " + f2.decoder + " * " + f3.decoder + " * " + f4.decoder;
                    };
                }
            );
        };

        @Override
        public <T, R> App<RecordDecoderBuilder.Mu<O>, R> map(final Function<? super T, ? extends R> func, final App<RecordDecoderBuilder.Mu<O>, T> ts) {
            final RecordDecoderBuilder<O, T> unbox = unbox(ts);
            return new RecordDecoderBuilder<>(
                unbox.decoder.map(func)
            );
        }
    }
}

