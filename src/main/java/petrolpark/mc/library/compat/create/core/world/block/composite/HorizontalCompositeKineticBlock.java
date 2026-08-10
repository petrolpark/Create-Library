package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

/**
 * Largely copied from {@link HorizontalKineticBlock Create source code}
 */
@ParametersAreNonnullByDefault
public abstract class HorizontalCompositeKineticBlock extends CompositeKineticBlock {

    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    public HorizontalCompositeKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(HORIZONTAL_FACING));
	};

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Direction preferred = AllBlocks.MECHANICAL_PRESS.get().getPreferredHorizontalFacing(context); // Just any old HorizontalKineticBlock - getPreferredHorizontalFacing isn't static
		if (preferred != null) return defaultBlockState().setValue(HORIZONTAL_FACING, preferred.getOpposite());
		return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	};

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	};

	@Override
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
	};
};
