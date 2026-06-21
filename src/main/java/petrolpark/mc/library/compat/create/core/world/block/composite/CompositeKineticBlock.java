package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CompositeKineticBlock extends Block implements ICompositeKineticBlock {

    public CompositeKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

	@Override
	public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean movedByPiston) {
		ICompositeKineticBlock.super.onPlace(state, level, pos, oldState, movedByPiston);
	};

	@Override
	public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	};

	@Override
	public void updateIndirectNeighbourShapes(@Nonnull BlockState stateIn, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, int flags, int count) {
		ICompositeKineticBlock.super.updateIndirectNeighbourShapes(stateIn, level, pos, flags, count);
	};

	@Override
	public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
		ICompositeKineticBlock.super.setPlacedBy(worldIn, pos, state, placer, stack);
	};
    
};
