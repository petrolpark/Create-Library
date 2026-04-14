package com.petrolpark.compat.create.common.kinetics.horseMill;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
		return movedContraption == null ? 0f : movedContraption.getEntityData().get(HorseMillContraptionEntity.GENERATED_STRESS_CAPACITY);
	};

	@Override
	public float getGeneratedSpeed() {
		if (movedContraption == null)
			return lastGeneratedSpeed;
		if (!running || movedContraption.isStalled())
			return 0f;
		return movedContraption.getEntityData().get(HorseMillContraptionEntity.GENERATED_SPEED);
	};

	@Override
	public ValueBoxTransform getMovementModeSlot() {
		return new HorseMillBearingBlockEntity.Slot();
	};

	public boolean isRunning() {
		return running;
	};

	public void assembleNextTick() {
		assembleNextTick = true;	
	};

	/**
	 * Copied from {@link MechanicalBearingBlockEntity#assemble Create source code}.
	 */
    @Override
    public void assemble() {
        final Level level = getLevel();
        if (level == null || !(level.getBlockState(worldPosition).getBlock() instanceof HorseMillBearingBlock)) return;

		final Direction direction = getBlockState().getValue(HorseMillBearingBlock.FACING);
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

	@Override
	@SuppressWarnings("null")
	public void attach(ControlledContraptionEntity contraptionEntity) {
		if (!(contraptionEntity instanceof HorseMillContraptionEntity)) return;
		if (!getBlockState().hasProperty(HorseMillBearingBlock.FACING)) return;

		movedContraption = contraptionEntity;
		setChanged();

		final BlockPos anchor = getBlockPos().relative(getBlockState().getValue(HorseMillBearingBlock.FACING));
		movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
		if (!getLevel().isClientSide()) {
			running = true;
			sendData();
		};
	};

	class Slot extends ValueBoxTransform.Sided {

		@Override
		protected Vec3 getSouthLocation() {
			return VecHelper.voxelSpace(8d, getBlockState().getValue(HorseMillBearingBlock.FACING) == Direction.UP ? 6d : 10d, 15.5d);
		};

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			return false; // Disabled
		};

	};
    
};
