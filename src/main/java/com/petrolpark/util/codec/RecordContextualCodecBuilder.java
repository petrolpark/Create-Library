package com.petrolpark.util.codec;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Copy of {@link RecordCodecBuilder} that accepts a context object when encoding and decoding
 */
public class RecordContextualCodecBuilder<CONTEXT, O, F> implements App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, F> {

    public static final class Mu<CONTEXT, O> implements K1 {};

    public static <CONTEXT, O, F> RecordContextualCodecBuilder<CONTEXT, O, F> unbox(final App<Mu<CONTEXT, O>, F> box) {
        return ((RecordContextualCodecBuilder<CONTEXT, O, F>) box);
    };

    private final BiFunction<CONTEXT, O, F> getter;
    private final BiFunction<CONTEXT, O, ContextualMapEncoder<CONTEXT, F>> encoder;
    private final ContextualMapDecoder<CONTEXT, F> decoder;

    private RecordContextualCodecBuilder(final BiFunction<CONTEXT, O, F> getter, final BiFunction<CONTEXT, O, ContextualMapEncoder<CONTEXT, F>> encoder, final ContextualMapDecoder<CONTEXT, F> decoder) {
        this.getter = getter;
        this.encoder = encoder;
        this.decoder = decoder;
    };

    public static <CONTEXT, O> Instance<CONTEXT, O> instance() {
        return new Instance<>();
    };

    public static <CONTEXT, O, F> RecordContextualCodecBuilder<CONTEXT, O, F> of(final BiFunction<CONTEXT, O, F> getter, final ContextualMapCodec<CONTEXT, F> codec) {
        return new RecordContextualCodecBuilder<>(getter, (c, o) -> codec, codec);
    }

    public static <CONTEXT, O, F> RecordContextualCodecBuilder<CONTEXT, O, F> point(final F instance) {
        return new RecordContextualCodecBuilder<>((c, o) -> instance, (c, o) -> ContextualMapEncoder.empty(), ContextualMapDecoder.unit(instance));
    };

    public static <CONTEXT, O> ContextualCodec<CONTEXT, O> create(final Function<Instance<CONTEXT, O>, ? extends App<Mu<CONTEXT, O>, O>> builder) {
        return build(builder.apply(instance())).codec();
    };

    public static <CONTEXT, O> ContextualMapCodec<CONTEXT, O> mapCodec(final Function<Instance<CONTEXT, O>, ? extends App<Mu<CONTEXT, O>, O>> builder) {
        return build(builder.apply(instance()));
    };

    public static <CONTEXT, O> ContextualMapCodec<CONTEXT, O> build(final App<Mu<CONTEXT, O>, O> builderBox) {
        final RecordContextualCodecBuilder<CONTEXT, O, O> builder = unbox(builderBox);
        return new ContextualMapCodec<CONTEXT, O>() {
            @Override
            public <T> DataResult<O> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
                return builder.decoder.decode(ops, context, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(final O input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                return builder.encoder.apply(context, input).encode(input, context, ops, prefix);
            }

            @Override
            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return builder.decoder.keys(ops);
            }

            @Override
            public String toString() {
                return "RecordCodec[" + builder.decoder + "]";
            }
        };
    };

    public static final class Instance<CONTEXT, O> implements Applicative<Mu<CONTEXT, O>, Instance.Mu<CONTEXT, O>> {

        private static final class Mu<CONTEXT, O> implements Applicative.Mu {};

        public RecordContextualCodecBuilder<CONTEXT, O, CONTEXT> context() {
            return new RecordContextualCodecBuilder<>((c, o) -> c, (c, o) -> ContextualMapEncoder.empty(), new ContextualMapDecoder.Implementation<>() {

                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.empty();
                };

                @Override
                public <T> DataResult<CONTEXT> decode(DynamicOps<T> ops, CONTEXT context, MapLike<T> input) {
                    return DataResult.success(context);
                };

            });
        };

        @Override
        public <A> App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, A> point(A a) {
            return RecordContextualCodecBuilder.point(a);
        };

        @Override
        public <A, R> Function<App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, A>, App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, R>> lift1(final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, Function<A, R>> function) {
            return fa -> {
                final RecordContextualCodecBuilder<CONTEXT, O, Function<A, R>> f = unbox(function);
                final RecordContextualCodecBuilder<CONTEXT, O, A> a = unbox(fa);

                return new RecordContextualCodecBuilder<>(
                    (c, o) -> f.getter.apply(c, o).apply(a.getter.apply(c, o)),
                    (c, o) -> {
                        final ContextualMapEncoder<CONTEXT, Function<A, R>> fEnc = f.encoder.apply(c, o);
                        final ContextualMapEncoder<CONTEXT, A> aEnc = a.encoder.apply(c, o);
                        final A aFromO = a.getter.apply(c, o);

                        return new ContextualMapEncoder.Implementation<CONTEXT, R>() {
                            @Override
                            public <T> RecordBuilder<T> encode(final R input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                                aEnc.encode(aFromO, context, ops, prefix);
                                fEnc.encode(a1 -> input, context, ops, prefix);
                                return prefix;
                            };

                            @Override
                            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                                return Stream.concat(aEnc.keys(ops), fEnc.keys(ops));
                            };

                            @Override
                            public String toString() {
                                return fEnc + " * " + aEnc;
                            };
                        };
                    },

                    new ContextualMapDecoder.Implementation<CONTEXT, R>() {

                        @Override
                        public <T> DataResult<R> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
                            return a.decoder.decode(ops, context, input).flatMap(ar ->
                                f.decoder.decode(ops, context, input).map(fr ->
                                    fr.apply(ar)
                                )
                            );
                        };

                        @Override
                        public <T> Stream<T> keys(final DynamicOps<T> ops) {
                            return Stream.concat(a.decoder.keys(ops), f.decoder.keys(ops));
                        };

                        @Override
                        public String toString() {
                            return f.decoder + " * " + a.decoder;
                        };
                    }
                );
            };
        }

        @Override
        public <A, B, R> App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, R> ap2(final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, BiFunction<A, B, R>> func, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, A> a, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, B> b) {
            final RecordContextualCodecBuilder<CONTEXT, O, BiFunction<A, B, R>> function = unbox(func);
            final RecordContextualCodecBuilder<CONTEXT, O, A> fa = unbox(a);
            final RecordContextualCodecBuilder<CONTEXT, O, B> fb = unbox(b);

            return new RecordContextualCodecBuilder<>(
                (c, o) -> function.getter.apply(c, o).apply(fa.getter.apply(c, o), fb.getter.apply(c, o)),
                (c, o) -> {
                    final ContextualMapEncoder<CONTEXT, BiFunction<A, B, R>> fEncoder = function.encoder.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, A> aEncoder = fa.encoder.apply(c, o);
                    final A aFromO = fa.getter.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, B> bEncoder = fb.encoder.apply(c, o);
                    final B bFromO = fb.getter.apply(c, o);

                    return new ContextualMapEncoder.Implementation<CONTEXT, R>() {

                        @Override
                        public <T> RecordBuilder<T> encode(final R input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                            aEncoder.encode(aFromO, context, ops, prefix);
                            bEncoder.encode(bFromO, context, ops, prefix);
                            fEncoder.encode((a1, b1) -> input, context, ops, prefix);
                            return prefix;
                        };

                        @Override
                        public <T> Stream<T> keys(final DynamicOps<T> ops) {
                            return Stream.of(
                                fEncoder.keys(ops),
                                aEncoder.keys(ops),
                                bEncoder.keys(ops)
                            ).flatMap(Function.identity());
                        };

                        @Override
                        public String toString() {
                            return fEncoder + " * " + aEncoder + " * " + bEncoder;
                        };
                    };
                },
                new ContextualMapDecoder.Implementation<CONTEXT, R>() {

                    @Override
                    public <T> DataResult<R> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
                        return DataResult.unbox(DataResult.instance().ap2(
                            function.decoder.decode(ops, context, input),
                            fa.decoder.decode(ops, context, input),
                            fb.decoder.decode(ops, context, input)
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
        }

        @Override
        public <T1, T2, T3, R> App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, R> ap3(final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, Function3<T1, T2, T3, R>> func, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T1> t1, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T2> t2, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T3> t3) {
            final RecordContextualCodecBuilder<CONTEXT, O, Function3<T1, T2, T3, R>> function = unbox(func);
            final RecordContextualCodecBuilder<CONTEXT, O, T1> f1 = unbox(t1);
            final RecordContextualCodecBuilder<CONTEXT, O, T2> f2 = unbox(t2);
            final RecordContextualCodecBuilder<CONTEXT, O, T3> f3 = unbox(t3);

            return new RecordContextualCodecBuilder<>(
                (c, o) -> function.getter.apply(c, o).apply(
                    f1.getter.apply(c, o),
                    f2.getter.apply(c, o),
                    f3.getter.apply(c, o)
                ),
                (c, o) -> {
                    final ContextualMapEncoder<CONTEXT, Function3<T1, T2, T3, R>> fEncoder = function.encoder.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, T1> e1 = f1.encoder.apply(c, o);
                    final T1 v1 = f1.getter.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, T2> e2 = f2.encoder.apply(c, o);
                    final T2 v2 = f2.getter.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, T3> e3 = f3.encoder.apply(c, o);
                    final T3 v3 = f3.getter.apply(c, o);

                    return new ContextualMapEncoder.Implementation<CONTEXT, R>() {
                        @Override
                        public <T> RecordBuilder<T> encode(final R input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                            e1.encode(v1, context, ops, prefix);
                            e2.encode(v2, context, ops, prefix);
                            e3.encode(v3, context, ops, prefix);
                            fEncoder.encode((t1, t2, t3) -> input, context, ops, prefix);
                            return prefix;
                        }

                        @Override
                        public <T> Stream<T> keys(final DynamicOps<T> ops) {
                            return Stream.of(
                                fEncoder.keys(ops),
                                e1.keys(ops),
                                e2.keys(ops),
                                e3.keys(ops)
                            ).flatMap(Function.identity());
                        }

                        @Override
                        public String toString() {
                            return fEncoder + " * " + e1 + " * " + e2 + " * " + e3;
                        }
                    };
                },
                new ContextualMapDecoder.Implementation<CONTEXT, R>() {
                    @Override
                    public <T> DataResult<R> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
                        return DataResult.unbox(DataResult.instance().ap3(
                            function.decoder.decode(ops, context, input),
                            f1.decoder.decode(ops, context, input),
                            f2.decoder.decode(ops, context, input),
                            f3.decoder.decode(ops, context, input)
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
                    }
                }
            );
        }

        @Override
        public <T1, T2, T3, T4, R> App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, R> ap4(final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, Function4<T1, T2, T3, T4, R>> func, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T1> t1, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T2> t2, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T3> t3, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T4> t4) {
            final RecordContextualCodecBuilder<CONTEXT, O, Function4<T1, T2, T3, T4, R>> function = unbox(func);
            final RecordContextualCodecBuilder<CONTEXT, O, T1> f1 = unbox(t1);
            final RecordContextualCodecBuilder<CONTEXT, O, T2> f2 = unbox(t2);
            final RecordContextualCodecBuilder<CONTEXT, O, T3> f3 = unbox(t3);
            final RecordContextualCodecBuilder<CONTEXT, O, T4> f4 = unbox(t4);

            return new RecordContextualCodecBuilder<>(
                (c, o) -> function.getter.apply(c, o).apply(
                    f1.getter.apply(c, o),
                    f2.getter.apply(c, o),
                    f3.getter.apply(c, o),
                    f4.getter.apply(c, o)
                ),
                (c, o) -> {
                    final ContextualMapEncoder<CONTEXT, Function4<T1, T2, T3, T4, R>> fEncoder = function.encoder.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, T1> e1 = f1.encoder.apply(c, o);
                    final T1 v1 = f1.getter.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, T2> e2 = f2.encoder.apply(c, o);
                    final T2 v2 = f2.getter.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, T3> e3 = f3.encoder.apply(c, o);
                    final T3 v3 = f3.getter.apply(c, o);
                    final ContextualMapEncoder<CONTEXT, T4> e4 = f4.encoder.apply(c, o);
                    final T4 v4 = f4.getter.apply(c, o);

                    return new ContextualMapEncoder.Implementation<CONTEXT, R>() {
                        @Override
                        public <T> RecordBuilder<T> encode(final R input, final CONTEXT context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                            e1.encode(v1, context, ops, prefix);
                            e2.encode(v2, context, ops, prefix);
                            e3.encode(v3, context, ops, prefix);
                            e4.encode(v4, context, ops, prefix);
                            fEncoder.encode((t1, t2, t3, t4) -> input, context, ops, prefix);
                            return prefix;
                        }

                        @Override
                        public <T> Stream<T> keys(final DynamicOps<T> ops) {
                            return Stream.of(
                                fEncoder.keys(ops),
                                e1.keys(ops),
                                e2.keys(ops),
                                e3.keys(ops),
                                e4.keys(ops)
                            ).flatMap(Function.identity());
                        }

                        @Override
                        public String toString() {
                            return fEncoder + " * " + e1 + " * " + e2 + " * " + e3 + " * " + e4;
                        }
                    };
                },
                new ContextualMapDecoder.Implementation<CONTEXT, R>() {
                    @Override
                    public <T> DataResult<R> decode(final DynamicOps<T> ops, final CONTEXT context, final MapLike<T> input) {
                        return DataResult.unbox(DataResult.instance().ap4(
                            function.decoder.decode(ops, context, input),
                            f1.decoder.decode(ops, context, input),
                            f2.decoder.decode(ops, context, input),
                            f3.decoder.decode(ops, context, input),
                            f4.decoder.decode(ops, context, input)
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
                    }

                    @Override
                    public String toString() {
                        return function.decoder + " * " + f1.decoder + " * " + f2.decoder + " * " + f3.decoder + " * " + f4.decoder;
                    }
                }
            );
        }

        @Override
        public <T, R> App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, R> map(final Function<? super T, ? extends R> func, final App<RecordContextualCodecBuilder.Mu<CONTEXT, O>, T> ts) {
            final RecordContextualCodecBuilder<CONTEXT, O, T> unbox = unbox(ts);
            final BiFunction<CONTEXT, O, T> getter = unbox.getter;
            return new RecordContextualCodecBuilder<>(
                getter.andThen(func),
                (c, o) -> new ContextualMapEncoder.Implementation<CONTEXT, R>() {
                    private final ContextualMapEncoder<CONTEXT, T> encoder = unbox.encoder.apply(c, o);

                    @Override
                    public <U> RecordBuilder<U> encode(final R input, final CONTEXT context, final DynamicOps<U> ops, final RecordBuilder<U> prefix) {
                        return encoder.encode(getter.apply(c, o), context, ops, prefix);
                    }

                    @Override
                    public <U> Stream<U> keys(final DynamicOps<U> ops) {
                        return encoder.keys(ops);
                    }

                    @Override
                    public String toString() {
                        return encoder + "[mapped]";
                    }
                },
                unbox.decoder.map(func)
            );
        }
    };
};
