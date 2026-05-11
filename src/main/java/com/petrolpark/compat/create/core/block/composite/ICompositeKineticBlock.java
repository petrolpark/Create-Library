package com.petrolpark.compat.create.core.block.composite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.compat.create.core.block.composite.CompositeKineticBlockEntity.CompositeKineticBlockEntityPart;
import com.simibubi.create.content.kinetics.base.IRotate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface ICompositeKineticBlock extends IRotate {

    @Override
    public default boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return false;
    };

	public default void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
		if (level.getBlockEntity(pos) instanceof CompositeKineticBlockEntity be) {
            boolean changed = oldState.getBlock() != state.getBlock() || state.hasBlockEntity() != oldState.hasBlockEntity() || !oldState.equals(state);
            for (CompositeKineticBlockEntityPart part : be.getParts()) {
                part.preventSpeedUpdate = 0;
                if (changed || !part.areStatesKineticallyEquivalent(oldState, state)) return;
                part.preventSpeedUpdate = 2;
            };
		};
	};
    
	public default void updateIndirectNeighbourShapes(@Nonnull BlockState stateIn, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, int flags, int count) {
		if (level.isClientSide()) return;
        if (level.getBlockEntity(pos) instanceof CompositeKineticBlockEntity be) be.removeExistingKineticInformation();
	};

	public default void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
		if (worldIn.isClientSide()) return;
        if (!(worldIn.getBlockEntity(pos) instanceof CompositeKineticBlockEntity be)) return;
		be.queueRotationIndicators();
	};
};
