package com.petrolpark.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class NetworkHelper {

    public static <OBJECT, FIELD> MapCodec<OBJECT> singleFieldMapCodec(Codec<FIELD> fieldCodec, String fieldName, Function<OBJECT, FIELD> getter, Function<FIELD, OBJECT> constructor) {
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

    public static <T extends ByteBuf, S extends Enum<S>> StreamCodec<T, S> enumStreamCodec(Class<S> clazz) {
        return new StreamCodec<>() {

            public @Nonnull S decode(@Nonnull T buffer) {
                return clazz.getEnumConstants()[VarInt.read(buffer)];
            };

            public void encode(@Nonnull T buffer, @Nonnull S value) {
                VarInt.write(buffer, value.ordinal());
            };
        };
    };

    public static <B extends ByteBuf, V> StreamCodec<B, List<V>> listStreamCodec(StreamCodec<B, V> base) {
        return base.apply(ByteBufCodecs.list());
    };
    
    public static Vec3 readVec3(FriendlyByteBuf buffer) {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    };

    public static void writeVec3(FriendlyByteBuf buffer, Vec3 vec) {
        buffer.writeDouble(vec.x).writeDouble(vec.y).writeDouble(vec.z);
    };

    public static BlockFace readBlockFace(FriendlyByteBuf buffer) {
        return BlockFace.of(buffer.readBlockPos(), buffer.readEnum(Direction.class));
    };

    public static void writeBlockFace(FriendlyByteBuf buffer, BlockFace face) {
        buffer.writeBlockPos(face.getPos()).writeEnum(face.getFace());
    };

    public static <T> void writeList(FriendlyByteBuf buffer, List<T> list, BiConsumer<FriendlyByteBuf, T> writer) {
        buffer.writeVarInt(list.size());
        list.forEach(e -> writer.accept(buffer, e));
    };

    public static <T> List<T> readList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> reader) {
        int size = buffer.readVarInt();
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(reader.apply(buffer));
        };
        return list;
    };
};
