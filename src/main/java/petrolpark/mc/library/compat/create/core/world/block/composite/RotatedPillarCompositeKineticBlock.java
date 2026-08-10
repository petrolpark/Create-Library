package petrolpark.mc.library.compat.create.core.world.block.composite;

import javax.annotation.Nonnull;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * Largely copied from {@link RotatedPillarKineticBlock Create source code}
 */
public abstract class RotatedPillarCompositeKineticBlock extends CompositeKineticBlock {
    
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public RotatedPillarCompositeKineticBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
			.setValue(AXIS, Direction.Axis.Y)
        );
	};

    @Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(AXIS));
	};

    @Override
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		final Axis preferredAxis = RotatedPillarKineticBlock.getPreferredAxis(context);
        final Player player = context.getPlayer();
		if (preferredAxis != null && (player == null || !player.isShiftKeyDown()))
			return defaultBlockState().setValue(AXIS, preferredAxis);
		return defaultBlockState().setValue(AXIS, preferredAxis != null && player != null && player.isShiftKeyDown()
            ? context.getClickedFace().getAxis()
			: context.getNearestLookingDirection().getAxis()
        );
	};

	@Override
	public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rot) {
		switch (rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                case X:
                    return state.setValue(AXIS, Direction.Axis.Z);
                case Z:
                    return state.setValue(AXIS, Direction.Axis.X);
                default:
                    return state;
                }
            default:
                return state;
		}
	};

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    };
};
