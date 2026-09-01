package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.Nonnull;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
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
 * Largely copied from {@link DirectionalKineticBlock}
 */
public abstract class DirectionalCompositeKineticBlock extends CompositeKineticBlock {
    
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public DirectionalCompositeKineticBlock(BlockBehaviour.Properties properties) {
		super(properties);
	};

	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FACING));
	};

	@Override
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		final Direction preferred = AllBlocks.ENCASED_FAN.get().getPreferredFacing(context); // Just any old HorizontalKineticBlock - getPreferredFacing isn't static
        final Player player = context.getPlayer();
		if (preferred == null || (player != null && player.isShiftKeyDown())) {
			final Direction nearestLookingDirection = context.getNearestLookingDirection();
			return defaultBlockState().setValue(FACING, player != null && player.isShiftKeyDown()
                ? nearestLookingDirection
                : nearestLookingDirection.getOpposite());
		};
		return defaultBlockState().setValue(FACING, preferred.getOpposite());
	};

	@Override
	public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	};

	@Override
	@SuppressWarnings("deprecation")
	public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	};
};
