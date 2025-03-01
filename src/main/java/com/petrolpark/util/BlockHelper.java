package com.petrolpark.util;

import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class BlockHelper {
    
    public static final Vec3i UNIT = new Vec3i(1, 1, 1);

    public static final Stream<BlockPos> betweenClosedExcludingEdges(BlockPos firstPos, BlockPos secondPos) {
        return BlockPos.betweenClosedStream(firstPos, secondPos).filter(coplanarWith(firstPos).or(coplanarWith(secondPos)));
    };

    public static final boolean coplanar(BlockPos pos1, BlockPos pos2) {
        return pos1.getX() == pos2.getX() || pos1.getY() == pos2.getY() || pos1.getZ() == pos2.getZ();
    };

    public static final Predicate<BlockPos> coplanarWith(BlockPos pos) {
        return p -> coplanar(pos, p);
    };
};
