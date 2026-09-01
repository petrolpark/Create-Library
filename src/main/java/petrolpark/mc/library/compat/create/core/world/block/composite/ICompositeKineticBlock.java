package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.IRotate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.core.world.block.composite.CompositeKineticBlockEntity.CompositeKineticBlockEntityPart;
import petrolpark.mc.library.compat.create.core.world.block.composite.CompositeKineticBlockEntity.GeneratingCompositeKineticBlockEntityPart;

public interface ICompositeKineticBlock extends IRotate {

    @Override
    public default InteractionResult onWrenched(BlockState state, UseOnContext context) {
        final Level level = context.getLevel();
		final BlockPos pos = context.getClickedPos();
		final BlockState rotated = getRotatedBlockState(state, context.getClickedFace());
		if (!rotated.canSurvive(level, context.getClickedPos())) return InteractionResult.PASS;

		switchToBlockState(level, pos, updateAfterWrenched(rotated, context));

		if (level.getBlockState(pos) != state) IWrenchable.playRotateSound(level, pos);

		return InteractionResult.SUCCESS;
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

    public static void switchToBlockState(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

		final BlockEntity be = level.getBlockEntity(pos);
		final BlockState currentState = level.getBlockState(pos);

		if (currentState == state) return;
		if (be == null || !(be instanceof CompositeKineticBlockEntity kbe)) {
			level.setBlock(pos, state, Block.UPDATE_ALL);
			return;
		};

        for (CompositeKineticBlockEntityPart part : kbe.getParts()) {
            if (!part.areStatesKineticallyEquivalent(currentState, state)) {
                if (part.hasNetwork()) part.getOrCreateNetwork().remove(part);
                part.detachKinetics();
                part.removeSource();
                if (part instanceof GeneratingCompositeKineticBlockEntityPart generatingPart) generatingPart.reActivateSource = true;
            };
        };

		level.setBlock(pos, state, Block.UPDATE_ALL);
    };
};
