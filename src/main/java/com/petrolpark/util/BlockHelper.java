package com.petrolpark.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

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

    public static final Stream<BlockState> streamMatching(BlockPredicate blockPredicate) {
        Stream<BlockState> stream = blockPredicate.blocks().stream().flatMap(HolderSet::stream).map(Holder::value)
            .map(Block::getStateDefinition)
            .map(StateDefinition::getPossibleStates)
            .flatMap(List::stream);
        if (blockPredicate.properties().isPresent()) stream = stream.filter(blockPredicate.properties().get()::matches);
        return stream;
    };


};
