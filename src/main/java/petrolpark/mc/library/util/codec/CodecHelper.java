package petrolpark.mc.library.util.codec;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.util.Lang;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.common.util.TriState;

public class CodecHelper {

    public static <OBJECT, FIELD> Codec<OBJECT> singleField(Codec<FIELD> fieldCodec, String fieldName, Function<OBJECT, FIELD> getter, Function<FIELD, OBJECT> constructor) {
        return RecordCodecBuilder.create(instance -> instance.group(
            fieldCodec.fieldOf(fieldName).forGetter(getter)
        ).apply(instance, constructor));
    };
    
    public static <OBJECT, FIELD> MapCodec<OBJECT> singleFieldMap(Codec<FIELD> fieldCodec, String fieldName, Function<OBJECT, FIELD> getter, Function<FIELD, OBJECT> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            fieldCodec.fieldOf(fieldName).forGetter(getter)
        ).apply(instance, constructor));
    };

    public static <OBJECT> Codec<List<OBJECT>> listOrSingle(Codec<OBJECT> codec) {
        return Codec.withAlternative(codec.listOf(), codec.flatComapMap(Collections::singletonList, list -> {
            if (list.isEmpty()) return DataResult.error(() -> "No "+codec.toString()+" in list");
            return DataResult.success(list.get(0));
        }));
    };

    public static final Codec<Integer> POS_INT = Codec.intRange(0, Integer.MAX_VALUE);

    public static final Codec<Float> POS_FLOAT = Codec.floatRange(0f, Float.MAX_VALUE);

    public static <T extends ByteBuf, S extends Enum<S>> StreamCodec<T, S> enumStream(Class<S> clazz) {
        return new StreamCodec<>() {

            public @Nonnull S decode(@Nonnull T buffer) {
                return clazz.getEnumConstants()[VarInt.read(buffer)];
            };

            public void encode(@Nonnull T buffer, @Nonnull S value) {
                VarInt.write(buffer, value.ordinal());
            };
        };
    };

    public static <B extends ByteBuf, V> StreamCodec<B, List<V>> listStream(StreamCodec<B, V> base) {
        return base.apply(ByteBufCodecs.list());
    };

    public static final Codec<Byte> byteRanged(byte minInclusive, byte maxInclusive) {
        final Function<Byte, DataResult<Byte>> checker = Codec.checkRange(minInclusive, maxInclusive);
        return Codec.BYTE.flatXmap(checker, checker);
    };

    public static final <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> compositeStreamCodec(
        final StreamCodec<? super B, T1> codec1,
        final Function<C, T1> getter1,
        final StreamCodec<? super B, T2> codec2,
        final Function<C, T2> getter2,
        final StreamCodec<? super B, T3> codec3,
        final Function<C, T3> getter3,
        final StreamCodec<? super B, T4> codec4,
        final Function<C, T4> getter4,
        final StreamCodec<? super B, T5> codec5,
        final Function<C, T5> getter5,
        final StreamCodec<? super B, T6> codec6,
        final Function<C, T6> getter6,
        final StreamCodec<? super B, T7> codec7,
        final Function<C, T7> getter7,
        final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(@Nonnull B byteBuf) {
                T1 t1 = codec1.decode(byteBuf);
                T2 t2 = codec2.decode(byteBuf);
                T3 t3 = codec3.decode(byteBuf);
                T4 t4 = codec4.decode(byteBuf);
                T5 t5 = codec5.decode(byteBuf);
                T6 t6 = codec6.decode(byteBuf);
                T7 t7 = codec7.decode(byteBuf);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            };

            @Override
            public void encode(@Nonnull B byteBuf, @Nonnull C object) {
                codec1.encode(byteBuf, getter1.apply(object));
                codec2.encode(byteBuf, getter2.apply(object));
                codec3.encode(byteBuf, getter3.apply(object));
                codec4.encode(byteBuf, getter4.apply(object));
                codec5.encode(byteBuf, getter5.apply(object));
                codec6.encode(byteBuf, getter6.apply(object));
                codec7.encode(byteBuf, getter7.apply(object));
            };
        };
    };

    public static final <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> compositeStreamCodec(
        final StreamCodec<? super B, T1> codec1,
        final Function<C, T1> getter1,
        final StreamCodec<? super B, T2> codec2,
        final Function<C, T2> getter2,
        final StreamCodec<? super B, T3> codec3,
        final Function<C, T3> getter3,
        final StreamCodec<? super B, T4> codec4,
        final Function<C, T4> getter4,
        final StreamCodec<? super B, T5> codec5,
        final Function<C, T5> getter5,
        final StreamCodec<? super B, T6> codec6,
        final Function<C, T6> getter6,
        final StreamCodec<? super B, T7> codec7,
        final Function<C, T7> getter7,
        final StreamCodec<? super B, T8> codec8,
        final Function<C, T8> getter8,
        final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(@Nonnull B byteBuf) {
                T1 t1 = codec1.decode(byteBuf);
                T2 t2 = codec2.decode(byteBuf);
                T3 t3 = codec3.decode(byteBuf);
                T4 t4 = codec4.decode(byteBuf);
                T5 t5 = codec5.decode(byteBuf);
                T6 t6 = codec6.decode(byteBuf);
                T7 t7 = codec7.decode(byteBuf);
                T8 t8 = codec8.decode(byteBuf);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            };

            @Override
            public void encode(@Nonnull B byteBuf, @Nonnull C object) {
                codec1.encode(byteBuf, getter1.apply(object));
                codec2.encode(byteBuf, getter2.apply(object));
                codec3.encode(byteBuf, getter3.apply(object));
                codec4.encode(byteBuf, getter4.apply(object));
                codec5.encode(byteBuf, getter5.apply(object));
                codec6.encode(byteBuf, getter6.apply(object));
                codec7.encode(byteBuf, getter7.apply(object));
                codec8.encode(byteBuf, getter8.apply(object));
            };
        };
    };

    public static final <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> compositeStreamCodec(
        final StreamCodec<? super B, T1> codec1,
        final Function<C, T1> getter1,
        final StreamCodec<? super B, T2> codec2,
        final Function<C, T2> getter2,
        final StreamCodec<? super B, T3> codec3,
        final Function<C, T3> getter3,
        final StreamCodec<? super B, T4> codec4,
        final Function<C, T4> getter4,
        final StreamCodec<? super B, T5> codec5,
        final Function<C, T5> getter5,
        final StreamCodec<? super B, T6> codec6,
        final Function<C, T6> getter6,
        final StreamCodec<? super B, T7> codec7,
        final Function<C, T7> getter7,
        final StreamCodec<? super B, T8> codec8,
        final Function<C, T8> getter8,
        final StreamCodec<? super B, T9> codec9,
        final Function<C, T9> getter9,
        final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(@Nonnull B byteBuf) {
                T1 t1 = codec1.decode(byteBuf);
                T2 t2 = codec2.decode(byteBuf);
                T3 t3 = codec3.decode(byteBuf);
                T4 t4 = codec4.decode(byteBuf);
                T5 t5 = codec5.decode(byteBuf);
                T6 t6 = codec6.decode(byteBuf);
                T7 t7 = codec7.decode(byteBuf);
                T8 t8 = codec8.decode(byteBuf);
                T9 t9 = codec9.decode(byteBuf);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            };

            @Override
            public void encode(@Nonnull B byteBuf, @Nonnull C object) {
                codec1.encode(byteBuf, getter1.apply(object));
                codec2.encode(byteBuf, getter2.apply(object));
                codec3.encode(byteBuf, getter3.apply(object));
                codec4.encode(byteBuf, getter4.apply(object));
                codec5.encode(byteBuf, getter5.apply(object));
                codec6.encode(byteBuf, getter6.apply(object));
                codec7.encode(byteBuf, getter7.apply(object));
                codec8.encode(byteBuf, getter8.apply(object));
                codec9.encode(byteBuf, getter9.apply(object));
            };
        };
    };

    public static final <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> StreamCodec<B, C> compositeStreamCodec(
        final StreamCodec<? super B, T1> codec1,
        final Function<C, T1> getter1,
        final StreamCodec<? super B, T2> codec2,
        final Function<C, T2> getter2,
        final StreamCodec<? super B, T3> codec3,
        final Function<C, T3> getter3,
        final StreamCodec<? super B, T4> codec4,
        final Function<C, T4> getter4,
        final StreamCodec<? super B, T5> codec5,
        final Function<C, T5> getter5,
        final StreamCodec<? super B, T6> codec6,
        final Function<C, T6> getter6,
        final StreamCodec<? super B, T7> codec7,
        final Function<C, T7> getter7,
        final StreamCodec<? super B, T8> codec8,
        final Function<C, T8> getter8,
        final StreamCodec<? super B, T9> codec9,
        final Function<C, T9> getter9,
        final StreamCodec<? super B, T10> codec10,
        final Function<C, T10> getter10,
        final Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(@Nonnull B byteBuf) {
                T1 t1 = codec1.decode(byteBuf);
                T2 t2 = codec2.decode(byteBuf);
                T3 t3 = codec3.decode(byteBuf);
                T4 t4 = codec4.decode(byteBuf);
                T5 t5 = codec5.decode(byteBuf);
                T6 t6 = codec6.decode(byteBuf);
                T7 t7 = codec7.decode(byteBuf);
                T8 t8 = codec8.decode(byteBuf);
                T9 t9 = codec9.decode(byteBuf);
                T10 t10 = codec10.decode(byteBuf);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
            };

            @Override
            public void encode(@Nonnull B byteBuf, @Nonnull C object) {
                codec1.encode(byteBuf, getter1.apply(object));
                codec2.encode(byteBuf, getter2.apply(object));
                codec3.encode(byteBuf, getter3.apply(object));
                codec4.encode(byteBuf, getter4.apply(object));
                codec5.encode(byteBuf, getter5.apply(object));
                codec6.encode(byteBuf, getter6.apply(object));
                codec7.encode(byteBuf, getter7.apply(object));
                codec8.encode(byteBuf, getter8.apply(object));
                codec9.encode(byteBuf, getter9.apply(object));
                codec10.encode(byteBuf, getter10.apply(object));
            };
        };
    };

    public static final <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> StreamCodec<B, C> compositeStreamCodec(
        final StreamCodec<? super B, T1> codec1,
        final Function<C, T1> getter1,
        final StreamCodec<? super B, T2> codec2,
        final Function<C, T2> getter2,
        final StreamCodec<? super B, T3> codec3,
        final Function<C, T3> getter3,
        final StreamCodec<? super B, T4> codec4,
        final Function<C, T4> getter4,
        final StreamCodec<? super B, T5> codec5,
        final Function<C, T5> getter5,
        final StreamCodec<? super B, T6> codec6,
        final Function<C, T6> getter6,
        final StreamCodec<? super B, T7> codec7,
        final Function<C, T7> getter7,
        final StreamCodec<? super B, T8> codec8,
        final Function<C, T8> getter8,
        final StreamCodec<? super B, T9> codec9,
        final Function<C, T9> getter9,
        final StreamCodec<? super B, T10> codec10,
        final Function<C, T10> getter10,
        final StreamCodec<? super B, T11> codec11,
        final Function<C, T11> getter11,
        final Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(@Nonnull B byteBuf) {
                T1 t1 = codec1.decode(byteBuf);
                T2 t2 = codec2.decode(byteBuf);
                T3 t3 = codec3.decode(byteBuf);
                T4 t4 = codec4.decode(byteBuf);
                T5 t5 = codec5.decode(byteBuf);
                T6 t6 = codec6.decode(byteBuf);
                T7 t7 = codec7.decode(byteBuf);
                T8 t8 = codec8.decode(byteBuf);
                T9 t9 = codec9.decode(byteBuf);
                T10 t10 = codec10.decode(byteBuf);
                T11 t11 = codec11.decode(byteBuf);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
            };

            @Override
            public void encode(@Nonnull B byteBuf, @Nonnull C object) {
                codec1.encode(byteBuf, getter1.apply(object));
                codec2.encode(byteBuf, getter2.apply(object));
                codec3.encode(byteBuf, getter3.apply(object));
                codec4.encode(byteBuf, getter4.apply(object));
                codec5.encode(byteBuf, getter5.apply(object));
                codec6.encode(byteBuf, getter6.apply(object));
                codec7.encode(byteBuf, getter7.apply(object));
                codec8.encode(byteBuf, getter8.apply(object));
                codec9.encode(byteBuf, getter9.apply(object));
                codec10.encode(byteBuf, getter10.apply(object));
                codec11.encode(byteBuf, getter11.apply(object));
            };
        };
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_STREAM_CODEC = ByteBufCodecs.registry(Registries.ITEM);

    public static final Codec<JsonElement> JSON_ELEMENT_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(JsonElement input, DynamicOps<T> ops, T prefix) {
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, input));
        };

        @Override
        public <T> DataResult<Pair<JsonElement, T>> decode(DynamicOps<T> ops, T input) {
            return DataResult.success(new Pair<>(ops.convertTo(JsonOps.INSTANCE, input), input));
        };

    };

    public static final StreamCodec<ByteBuf, MinMaxBounds.Ints> INT_BOUNDS_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ByteBufCodecs.INT), MinMaxBounds.Ints::min,
        ByteBufCodecs.optional(ByteBufCodecs.INT), MinMaxBounds.Ints::max,
        (min, max) -> new MinMaxBounds.Ints(min, max, min.map(m -> m.longValue() * m.longValue()), max.map(m -> m.longValue() * m.longValue()))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentPredicate> ENCHANTMENT_PREDICATE_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.ENCHANTMENT)), EnchantmentPredicate::enchantments,
        INT_BOUNDS_STREAM_CODEC, EnchantmentPredicate::level,
        EnchantmentPredicate::new
    );

    public static final StreamCodec<FriendlyByteBuf, ChunkPos> CHUNK_POS_STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeChunkPos, FriendlyByteBuf::readChunkPos);

    public static final StreamCodec<ByteBuf, WoodType> WOOD_TYPE_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(WoodType.TYPES::get, WoodType::name);

    public static final Codec<Markings> HORSE_MARKINGS_CODEC = Codec.stringResolver(markings -> Lang.asId(markings.name()), name -> Stream.of(Markings.values()).filter(markings -> Lang.asId(markings.name()).equals(name)).findFirst().orElse(null));

    public static final StreamCodec<ByteBuf, TriState> TRI_STATE_STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(TriState.class);

    public static final StreamCodec<ByteBuf, Rotation> ROTATION_STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Rotation.class);
};
