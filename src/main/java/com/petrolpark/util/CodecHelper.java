package com.petrolpark.util;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

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
};
