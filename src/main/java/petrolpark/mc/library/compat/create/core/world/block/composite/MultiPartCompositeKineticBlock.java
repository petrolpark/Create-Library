package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.core.world.block.multiPart.CreateMultiPartBlock;

@ParametersAreNonnullByDefault
public abstract class MultiPartCompositeKineticBlock<PART extends CreateMultiPartBlock.ICreatePart> extends CreateMultiPartBlock<PART> implements ICompositeKineticBlock {

    public MultiPartCompositeKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

	@Override
	protected void switchBlockState(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
		ICompositeKineticBlock.switchToBlockState(level, pos, newState);
	};

    @Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		ICompositeKineticBlock.super.onPlace(state, level, pos, oldState, movedByPiston);
	};

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	};

	@Override
	public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor level, BlockPos pos, int flags, int count) {
		ICompositeKineticBlock.super.updateIndirectNeighbourShapes(stateIn, level, pos, flags, count);
	};

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		ICompositeKineticBlock.super.setPlacedBy(worldIn, pos, state, placer, stack);
	};
};
