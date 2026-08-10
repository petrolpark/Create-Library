package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.Nonnull;

import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class HorizontalAxisCompositeKineticBlock extends CompositeKineticBlock {
    
    public static final EnumProperty<Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	public HorizontalAxisCompositeKineticBlock(Properties properties) {
		super(properties);
	};

	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(HORIZONTAL_AXIS));
	};

	@Override
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		final Axis preferredAxis = HorizontalAxisKineticBlock.getPreferredHorizontalAxis(context);
		if (preferredAxis != null) return defaultBlockState().setValue(HORIZONTAL_AXIS, preferredAxis);
		return defaultBlockState().setValue(HORIZONTAL_AXIS, context.getHorizontalDirection().getClockWise().getAxis());
	};

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_AXIS);
	};

	@Override
	public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rot) {
		final Axis axis = state.getValue(HORIZONTAL_AXIS);
		return state.setValue(HORIZONTAL_AXIS, rot.rotate(Direction.get(AxisDirection.POSITIVE, axis)).getAxis());
	};

	@Override
	public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirrorIn) {
		return state;
	};
};
