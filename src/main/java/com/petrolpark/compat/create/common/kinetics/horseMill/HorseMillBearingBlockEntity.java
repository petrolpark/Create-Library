package com.petrolpark.compat.create.common.kinetics.horseMill;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HorseMillBearingBlockEntity extends WindmillBearingBlockEntity {

    public HorseMillBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

	@Override
	public float calculateAddedStressCapacity() {
		return 1f; //TODO
	};

	@Override
	public ValueBoxTransform getMovementModeSlot() {
		return new HorseMillBearingBlockEntity.Slot();
	};

	/**
	 * Copied from {@link MechanicalBearingBlockEntity#assemble Create source code}.
	 */
    @Override
    public void assemble() {
        final Level level = getLevel();
        if (level == null || !(level.getBlockState(worldPosition).getBlock() instanceof HorseMillBearingBlock)) return;

		final Direction direction = getBlockState().getValue(BearingBlock.FACING);
		final HorseMillContraption contraption = new HorseMillContraption(direction);

		try {
			if (!contraption.assemble(level, worldPosition)) return;
			lastException = null;
		} catch (AssemblyException e) {
			lastException = e;
			sendData();
			return;
		};

        //TODO advancement

		contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
		movedContraption = HorseMillContraptionEntity.create(level, this, contraption);
		final BlockPos anchor = getBlockPos().relative(direction);
		movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
		movedContraption.setRotationAxis(direction.getAxis());
		level.addFreshEntity(movedContraption);

		AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

		if (contraption.containsBlockBreakers()) award(AllAdvancements.CONTRAPTION_ACTORS);

		running = true;
		angle = 0;
		sendData();
		updateGeneratedRotation();
    };

	class Slot extends ValueBoxTransform.Sided {

		@Override
		protected Vec3 getSouthLocation() {
			return new Vec3(8d, getBlockState().getValue(HorseMillBearingBlock.FACING) == Direction.UP ? 6d : 10d, 15.5d);
		};

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			return direction.getAxis() != Axis.Y;
		};

	};
    
};
