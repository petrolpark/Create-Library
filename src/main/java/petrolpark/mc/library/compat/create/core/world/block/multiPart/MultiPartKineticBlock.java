package petrolpark.mc.library.compat.create.core.world.block.multiPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.core.world.block.multiPart.CreateMultiPartBlock.ICreatePart;

/**
 * Largely copied from {@link KineticBlock Create source code}.
 */
public abstract class MultiPartKineticBlock<PART extends ICreatePart> extends CreateMultiPartBlock<PART> implements IRotate {

    protected MultiPartKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
	public void onPlace(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
		if (worldIn.getBlockEntity(pos) instanceof KineticBlockEntity kineticBlockEntity) {
			kineticBlockEntity.preventSpeedUpdate = 0;
			if (oldState.getBlock() != state.getBlock()) return;
			if (state.hasBlockEntity() != oldState.hasBlockEntity()) return;
			if (!areStatesKineticallyEquivalent(oldState, state)) return;
			kineticBlockEntity.preventSpeedUpdate = 2;
		};
	};

	@Override
	public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	};

	@Override
	public void switchBlockState(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, @Nonnull BlockState newState) {
		if (!level.isClientSide() && (
				newState.getBlock() != this ||
				!areStatesKineticallyEquivalent(oldState, newState)
			) && level.getBlockEntity(pos) instanceof KineticBlockEntity kineticBlockEntity
		) {
			if (kineticBlockEntity.hasNetwork()) kineticBlockEntity.getOrCreateNetwork().remove(kineticBlockEntity);
			kineticBlockEntity.detachKinetics();
			kineticBlockEntity.removeSource();
		};
		// This static method won't do the above (detach kinetics etc.) as it looks for KineticBlock, which MultiPartKineticBlock does not extend
		KineticBlockEntity.switchToBlockState(level, pos, newState);
	};

	protected abstract boolean areStatesKineticallyEquivalent(@Nonnull BlockState oldState, @Nonnull BlockState newState);

	@Override
	public void updateIndirectNeighbourShapes(@Nonnull BlockState stateIn, @Nonnull LevelAccessor worldIn, @Nonnull BlockPos pos, int flags, int count) {
		if (worldIn.isClientSide()) return;
		if (!(worldIn.getBlockEntity(pos) instanceof KineticBlockEntity kbe)) return;
		if (kbe.preventSpeedUpdate > 0) return;

		// Remove previous information when block is added
		kbe.warnOfMovement();
		kbe.clearKineticInformation();
		kbe.updateSpeed = true;
	};

	@Override
	public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
		// if (worldIn.isClientSide()) return;
		// if (!(worldIn.getBlockEntity(pos) instanceof KineticBlockEntity kbe)) return;

		// kbe.effects.queueRotationIndicators();
	};

	public float getParticleTargetRadius() {
		return 0.65f;
	};

	public float getParticleInitialRadius() {
		return 0.75f;
	};
};
