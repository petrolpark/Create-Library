package com.petrolpark.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.Suppliers;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockHelper {
    
    public static final Vec3i UNIT = new Vec3i(1, 1, 1);

    public static final Supplier<Block> supplier(ResourceLocation id) {
        return Suppliers.memoize(() -> BuiltInRegistries.BLOCK.get(id));
    };

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

    public static final boolean equals(BlockState s1, BlockState s2) {
        return s1.getBlock() == s2.getBlock() && s1.getValues().equals(s2.getValues());
    };

    @Nullable
    public static final Block getBlock(Object obj) {
        if (obj instanceof Holder holder) return getBlock(holder.value());
        if (obj instanceof Block block) return block;
        else if (obj instanceof BlockState state) return state.getBlock();
        else {
            final Item potentialItem;
            if (obj instanceof ItemLike item) potentialItem = item.asItem();
            else if (obj instanceof ItemStack stack) potentialItem = stack.getItem();
            else return null;
            if (potentialItem instanceof BlockItem blockItem) return blockItem.getBlock();
            else return null;
        }
    };

    public static final <T extends Comparable<T>> BlockState copyUnchecked(BlockState base, BlockState toCopy, Property<T> property) {
        return base.setValue(property, toCopy.getValue(property));
    };

    public static final BlockState copyAll(BlockState base, BlockState toCopy) {
        for (Property<?> property : toCopy.getProperties()) {
            if (base.hasProperty(property)) base = copyUnchecked(base, toCopy, property);
        };
        return base;
    };


};
