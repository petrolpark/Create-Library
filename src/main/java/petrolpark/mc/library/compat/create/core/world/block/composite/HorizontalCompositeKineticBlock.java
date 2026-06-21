package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;

import net.createmod.catnip.data.Iterate;
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

@ParametersAreNonnullByDefault
public abstract class HorizontalCompositeKineticBlock extends CompositeKineticBlock {

    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    public HorizontalCompositeKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
		super.createBlockStateDefinition(builder);
	};

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Direction preferred = getPreferredHorizontalFacing(context);
		if (preferred != null) return defaultBlockState().setValue(HORIZONTAL_FACING, preferred.getOpposite());
		return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	};

    /**
     * Copied from {@link HorizontalKineticBlock#getPreferredHorizontalFacing Creat source code}
     */
	public Direction getPreferredHorizontalFacing(BlockPlaceContext context) {
		Direction preferredSide = null;
		for (Direction side : Iterate.horizontalDirections) {
			BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
			if (blockState.getBlock() instanceof IRotate rotate) {
				if (rotate.hasShaftTowards(context.getLevel(), context.getClickedPos().relative(side), blockState, side.getOpposite()))
					if (preferredSide != null && preferredSide.getAxis() != side.getAxis()) {
						preferredSide = null;
						break;
					} else {
						preferredSide = side;
					};
			};
		};
		return preferredSide;
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
