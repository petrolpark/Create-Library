package com.petrolpark.util.codec;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Copy of {@link StreamCodec} that accepts a context object when encoding and decoding
 */
public interface ContextualStreamCodec<B, CONTEXT, V> {

    void encode(B buffer, CONTEXT context, V value);
    
    V decode(B buffer, CONTEXT context);

    public static <B, CONTEXT, V> ContextualStreamCodec<B, CONTEXT, V> of(StreamCodec<B, V> streamCodec) {
        return new ContextualStreamCodec<B, CONTEXT, V>() {
            @Override
            public V decode(B buffer, CONTEXT context) {
                return streamCodec.decode(buffer);
            };

            @Override
            public void encode(B buffer, CONTEXT context, V value) {
                streamCodec.encode(buffer, value);
            };
        };
    };

    public static <B, CONTEXT, V> ContextualStreamCodec<B, CONTEXT, V> of(Function<CONTEXT, V> factory) {
        return new ContextualStreamCodec<B,CONTEXT,V>() {
            @Override
            public void encode(B buffer, CONTEXT context, V value) {
            
            };

            @Override
            public V decode(B buffer, CONTEXT context) {
                return factory.apply(context);
            };
        };
    };

    public static <B, CONTEXT, V> ContextualStreamCodec<B, CONTEXT, V> unit(final V expectedValue) {
        return new ContextualStreamCodec<B, CONTEXT, V>() {
            @Override
            public V decode(B buffer, CONTEXT context) {
                return expectedValue;
            };

            @Override
            public void encode(B buffer, CONTEXT context, V value) {
                if (!value.equals(expectedValue)) throw new IllegalStateException("Can't encode '" + value + "', expected '" + expectedValue + "'");
            };
        };
    }

    public static <B extends ByteBuf, CONTEXT, V> ContextualStreamCodec<B, CONTEXT, Optional<V>> optional(final StreamCodec<B, V> codec) {
        return new ContextualStreamCodec<B, CONTEXT, Optional<V>>() {

            public Optional<V> decode(B buffer, CONTEXT context) {
                return buffer.readBoolean() ? Optional.of(codec.decode(buffer)) : Optional.empty();
            };

            public void encode(B buffer, CONTEXT context, Optional<V> value) {
                if (value.isPresent()) {
                    buffer.writeBoolean(true);
                    codec.encode(buffer, value.get());
                } else {
                    buffer.writeBoolean(false);
                };
            };
        };
    };

    public static <B extends ByteBuf, CONTEXT, V> ContextualStreamCodec<B, CONTEXT, Optional<V>> optional(final ContextualStreamCodec<B, CONTEXT, V> codec) {
        return new ContextualStreamCodec<B, CONTEXT, Optional<V>>() {

            public Optional<V> decode(B buffer, CONTEXT context) {
                return buffer.readBoolean() ? Optional.of(codec.decode(buffer, context)) : Optional.empty();
            };

            public void encode(B buffer, CONTEXT context, Optional<V> value) {
                if (value.isPresent()) {
                    buffer.writeBoolean(true);
                    codec.encode(buffer, context, value.get());
                } else {
                    buffer.writeBoolean(false);
                };
            };
        };
    };

    public default <O> ContextualStreamCodec<B, CONTEXT, O> map(final Function<? super V, ? extends O> factory, final Function<? super O, ? extends V> getter) {
        
        return new ContextualStreamCodec<B, CONTEXT, O>() {

            @Override
            public O decode(B buffer, CONTEXT context) {
                return (O)factory.apply(ContextualStreamCodec.this.decode(buffer, context));
            };

            @Override
            public void encode(B buffer, CONTEXT context, O value) {
                ContextualStreamCodec.this.encode(buffer, context, (V)getter.apply(value));
            };
        };
    };

    public default <O> ContextualStreamCodec<B, CONTEXT, O> map(final Function<? super V, ? extends O> factory, final BiFunction<? super O, CONTEXT, ? extends V> getter) {
        
        return new ContextualStreamCodec<B, CONTEXT, O>() {

            @Override
            public O decode(B buffer, CONTEXT context) {
                return (O)factory.apply(ContextualStreamCodec.this.decode(buffer, context));
            };

            @Override
            public void encode(B buffer, CONTEXT context, O value) {
                ContextualStreamCodec.this.encode(buffer, context, (V)getter.apply(value, context));
            };
        };
    };

    public default <O> ContextualStreamCodec<B, CONTEXT, O> map(final BiFunction<? super V, CONTEXT, ? extends O> factory, final Function<? super O, ? extends V> getter) {
        
        return new ContextualStreamCodec<B, CONTEXT, O>() {

            @Override
            public O decode(B buffer, CONTEXT context) {
                return (O)factory.apply(ContextualStreamCodec.this.decode(buffer, context), context);
            };

            @Override
            public void encode(B buffer, CONTEXT context, O value) {
                ContextualStreamCodec.this.encode(buffer, context, (V)getter.apply(value));
            };
        };
    };

    public default <O> ContextualStreamCodec<B, CONTEXT, O> map(final BiFunction<? super V, CONTEXT, ? extends O> factory, final BiFunction<? super O, CONTEXT, ? extends V> getter) {
        
        return new ContextualStreamCodec<B, CONTEXT, O>() {

            @Override
            public O decode(B buffer, CONTEXT context) {
                return (O)factory.apply(ContextualStreamCodec.this.decode(buffer, context), context);
            };

            @Override
            public void encode(B buffer, CONTEXT context, O value) {
                ContextualStreamCodec.this.encode(buffer, context, (V)getter.apply(value, context));
            };
        };
    };

    @SuppressWarnings("unchecked")
    public static <B, CONTEXT, V, U> ContextualStreamCodec<B, CONTEXT, U> dispatch(
        final StreamCodec<B, V> typeCodec,
        final Function<? super U, ? extends V> keyGetter, final Function<? super V, ? extends ContextualStreamCodec<? super B, CONTEXT, ? extends U>> codecGetter
    ) {
        return new ContextualStreamCodec<B, CONTEXT, U>() {
            @Override
            public U decode(B byteBuffer, CONTEXT context) {
                final V v = typeCodec.decode(byteBuffer);
                final ContextualStreamCodec<? super B, CONTEXT, ? extends U> streamcodec = (ContextualStreamCodec<? super B, CONTEXT, ? extends U>)codecGetter.apply(v);
                return (U)streamcodec.decode(byteBuffer, context);
            };

            @Override
            public void encode(B byteBuffer, CONTEXT context, U value) {
                final V v = (V)keyGetter.apply(value);
                final ContextualStreamCodec<B, CONTEXT, U> streamcodec = (ContextualStreamCodec<B, CONTEXT, U>)codecGetter.apply(v);
                typeCodec.encode(byteBuffer, v);
                streamcodec.encode(byteBuffer, context, value);
            };
        };
    };
    
    public static <B, CONTEXT, C, T1> ContextualStreamCodec<B, CONTEXT, C> composite(final ContextualStreamCodec<? super B, CONTEXT, T1> codec, final Function<C, T1> getter, final Function<T1, C> factory) {
        return new ContextualStreamCodec<B, CONTEXT, C>() {
            @Override
            public C decode(B buffer, CONTEXT context) {
                T1 t1 = codec.decode(buffer, context);
                return factory.apply(t1);
            };

            @Override
            public void encode(B buffer, CONTEXT context, C value) {
                codec.encode(buffer, context, getter.apply(value));
            };
        };
    }

    public static <B, CONTEXT, C, T1, T2> ContextualStreamCodec<B, CONTEXT, C> composite(
        final ContextualStreamCodec<? super B, CONTEXT, T1> codec1,
        final Function<C, T1> getter1,
        final ContextualStreamCodec<? super B, CONTEXT, T2> codec2,
        final Function<C, T2> getter2,
        final BiFunction<T1, T2, C> factory
    ) {
        return new ContextualStreamCodec<B, CONTEXT, C>() {
            @Override
            public C decode(B buffer, CONTEXT context) {
                T1 t1 = codec1.decode(buffer, context);
                T2 t2 = codec2.decode(buffer, context);
                return factory.apply(t1, t2);
            };

            @Override
            public void encode(B buffer, CONTEXT context, C value) {
                codec1.encode(buffer, context, getter1.apply(value));
                codec2.encode(buffer, context, getter2.apply(value));
            };
        };
    }

    public static <B, CONTEXT, C, T1, T2, T3> ContextualStreamCodec<B, CONTEXT, C> composite(
        final ContextualStreamCodec<? super B, CONTEXT, T1> codec1,
        final Function<C, T1> getter1,
        final ContextualStreamCodec<? super B, CONTEXT, T2> codec2,
        final Function<C, T2> getter2,
        final ContextualStreamCodec<? super B, CONTEXT, T3> codec3,
        final Function<C, T3> getter3,
        final Function3<T1, T2, T3, C> factory
    ) {
        return new ContextualStreamCodec<B, CONTEXT, C>() {
            @Override
            public C decode(B buffer, CONTEXT context) {
                T1 t1 = codec1.decode(buffer, context);
                T2 t2 = codec2.decode(buffer, context);
                T3 t3 = codec3.decode(buffer, context);
                return factory.apply(t1, t2, t3);
            }

            @Override
            public void encode(B buffer, CONTEXT context, C value) {
                codec1.encode(buffer, context, getter1.apply(value));
                codec2.encode(buffer, context, getter2.apply(value));
                codec3.encode(buffer, context, getter3.apply(value));
            }
        };
    }

    public static <B, CONTEXT, C, T1, T2, T3, T4> ContextualStreamCodec<B, CONTEXT, C> composite(
        final ContextualStreamCodec<? super B, CONTEXT, T1> codec1,
        final Function<C, T1> getter1,
        final ContextualStreamCodec<? super B, CONTEXT, T2> codec2,
        final Function<C, T2> getter2,
        final ContextualStreamCodec<? super B, CONTEXT, T3> codec3,
        final Function<C, T3> getter3,
        final ContextualStreamCodec<? super B, CONTEXT, T4> codec4,
        final Function<C, T4> getter4,
        final Function4<T1, T2, T3, T4, C> factory
    ) {
        return new ContextualStreamCodec<B, CONTEXT, C>() {
            @Override
            public C decode(B buffer, CONTEXT context) {
                T1 t1 = codec1.decode(buffer, context);
                T2 t2 = codec2.decode(buffer, context);
                T3 t3 = codec3.decode(buffer, context);
                T4 t4 = codec4.decode(buffer, context);
                return factory.apply(t1, t2, t3, t4);
            }

            @Override
            public void encode(B buffer, CONTEXT context, C value) {
                codec1.encode(buffer, context, getter1.apply(value));
                codec2.encode(buffer, context, getter2.apply(value));
                codec3.encode(buffer, context, getter3.apply(value));
                codec4.encode(buffer, context, getter4.apply(value));
            }
        };
    }

    public static <B, CONTEXT, C, T1, T2, T3, T4, T5> ContextualStreamCodec<B, CONTEXT, C> composite(
        final ContextualStreamCodec<? super B, CONTEXT, T1> codec1,
        final Function<C, T1> getter1,
        final ContextualStreamCodec<? super B, CONTEXT, T2> codec2,
        final Function<C, T2> getter2,
        final ContextualStreamCodec<? super B, CONTEXT, T3> codec3,
        final Function<C, T3> getter3,
        final ContextualStreamCodec<? super B, CONTEXT, T4> codec4,
        final Function<C, T4> getter4,
        final ContextualStreamCodec<? super B, CONTEXT, T5> codec5,
        final Function<C, T5> getter5,
        final Function5<T1, T2, T3, T4, T5, C> factory
    ) {
        return new ContextualStreamCodec<B, CONTEXT, C>() {
            @Override
            public C decode(B buffer, CONTEXT context) {
                T1 t1 = codec1.decode(buffer, context);
                T2 t2 = codec2.decode(buffer, context);
                T3 t3 = codec3.decode(buffer, context);
                T4 t4 = codec4.decode(buffer, context);
                T5 t5 = codec5.decode(buffer, context);
                return factory.apply(t1, t2, t3, t4, t5);
            }

            @Override
            public void encode(B buffer, CONTEXT context, C value) {
                codec1.encode(buffer, context, getter1.apply(value));
                codec2.encode(buffer, context, getter2.apply(value));
                codec3.encode(buffer, context, getter3.apply(value));
                codec4.encode(buffer, context, getter4.apply(value));
                codec5.encode(buffer, context, getter5.apply(value));
            }
        };
    }

    public static <B, CONTEXT, C, T1, T2, T3, T4, T5, T6> ContextualStreamCodec<B, CONTEXT, C> composite(
        final ContextualStreamCodec<? super B, CONTEXT, T1> codec1,
        final Function<C, T1> getter1,
        final ContextualStreamCodec<? super B, CONTEXT, T2> codec2,
        final Function<C, T2> getter2,
        final ContextualStreamCodec<? super B, CONTEXT, T3> codec3,
        final Function<C, T3> getter3,
        final ContextualStreamCodec<? super B, CONTEXT, T4> codec4,
        final Function<C, T4> getter4,
        final ContextualStreamCodec<? super B, CONTEXT, T5> codec5,
        final Function<C, T5> getter5,
        final ContextualStreamCodec<? super B, CONTEXT, T6> codec6,
        final Function<C, T6> getter6,
        final Function6<T1, T2, T3, T4, T5, T6, C> factory
    ) {
        return new ContextualStreamCodec<B, CONTEXT, C>() {
            @Override
            public C decode(B buffer, CONTEXT context) {
                T1 t1 = codec1.decode(buffer, context);
                T2 t2 = codec2.decode(buffer, context);
                T3 t3 = codec3.decode(buffer, context);
                T4 t4 = codec4.decode(buffer, context);
                T5 t5 = codec5.decode(buffer, context);
                T6 t6 = codec6.decode(buffer, context);
                return factory.apply(t1, t2, t3, t4, t5, t6);
            }

            @Override
            public void encode(B buffer, CONTEXT context, C value) {
                codec1.encode(buffer, context, getter1.apply(value));
                codec2.encode(buffer, context, getter2.apply(value));
                codec3.encode(buffer, context, getter3.apply(value));
                codec4.encode(buffer, context, getter4.apply(value));
                codec5.encode(buffer, context, getter5.apply(value));
                codec6.encode(buffer, context, getter6.apply(value));
            }
        };
    };
};
