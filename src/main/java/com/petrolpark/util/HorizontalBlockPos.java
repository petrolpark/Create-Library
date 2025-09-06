package com.petrolpark.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Rotation;

public record HorizontalBlockPos(int x, int z) {

    public static final Codec<HorizontalBlockPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("x").forGetter(HorizontalBlockPos::x),
        Codec.INT.fieldOf("z").forGetter(HorizontalBlockPos::z)
    ).apply(instance, HorizontalBlockPos::new));

    public static final StreamCodec<ByteBuf, HorizontalBlockPos> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, HorizontalBlockPos::x,
        ByteBufCodecs.INT, HorizontalBlockPos::z,
        HorizontalBlockPos::new
    );

    public static HorizontalBlockPos of(BlockPos pos) {
        return new HorizontalBlockPos(pos.getX(), pos.getZ());
    };

    public boolean is(BlockPos pos) {
        return pos.getX() == x() && pos.getZ() == z();
    };
    
    public HorizontalBlockPos offset(int x, int z) {
        return new HorizontalBlockPos(x() + x, z() + z);
    };

    public HorizontalBlockPos offset(HorizontalBlockPos vector) {
        return offset(vector.x(), vector.z());
    };

    public HorizontalBlockPos subtract(HorizontalBlockPos vector) {
        return offset(-vector.x(), -vector.z());
    };

    public HorizontalBlockPos multiply(int scalar) {
        return new HorizontalBlockPos(x() * scalar, z() * scalar);
    };

    public HorizontalBlockPos north() {
        return relative(Direction.NORTH);
    };

    public HorizontalBlockPos north(int distance) {
        return relative(Direction.NORTH, distance);
    };

    public HorizontalBlockPos south() {
        return relative(Direction.SOUTH);
    };

    public HorizontalBlockPos south(int distance) {
        return relative(Direction.SOUTH, distance);
    };

    public HorizontalBlockPos west() {
        return relative(Direction.WEST);
    };

    public HorizontalBlockPos west(int distance) {
        return relative(Direction.WEST, distance);
    };

    public HorizontalBlockPos east() {
        return relative(Direction.EAST);
    };

    public HorizontalBlockPos east(int distance) {
        return relative(Direction.EAST, distance);
    };

    public HorizontalBlockPos relative(Direction direction) {
        return relative(direction, 1);
    };

    public HorizontalBlockPos relative(Direction direction, int distance) {
        if (direction.getAxis() == Direction.Axis.Y) throw new IllegalArgumentException("Cannot offset HorizontalBlockPos vertically");
        return distance == 0 ? this : new HorizontalBlockPos(x() + direction.getStepX() * distance, z() + direction.getStepZ() * distance);
    };

    public HorizontalBlockPos rotate(Rotation rotation) {
        switch (rotation) {
            case NONE:
            default:
                return this;
            case CLOCKWISE_90:
                return new HorizontalBlockPos(-z(), x());
            case CLOCKWISE_180:
                return new HorizontalBlockPos(-x(), -z());
            case COUNTERCLOCKWISE_90:
                return new HorizontalBlockPos(z(), -x());
        }
    };

    public int distanceToSqr(HorizontalBlockPos pos) {
        return (x() - pos.x()) * (x() - pos.x()) + (z() - pos.z()) * (z() - pos.z());
    };

    public Optional<HorizontalBlockPos> closest(Collection<HorizontalBlockPos> otherPositions) {
        return otherPositions.stream().min(Comparator.comparingInt(this::distanceToSqr));
    };

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof HorizontalBlockPos pos) return x() == pos.x() && z == pos.z();
        return false;
    };
};
