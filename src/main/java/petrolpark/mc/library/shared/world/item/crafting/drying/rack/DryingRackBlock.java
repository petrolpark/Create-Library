package petrolpark.mc.library.shared.world.item.crafting.drying.rack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedBlockEntityTypes;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DryingRackBlock extends Block implements EntityBlock, ISharedFeature {

    public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public static final VoxelShaper SHAPE = VoxelShaper.forHorizontalAxis(Block.box(7f, 0f, 0f, 9f, 16f, 16f), Axis.Z);

    public DryingRackBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        final BlockState stateForPlacement = super.getStateForPlacement(context);
		if (stateForPlacement == null) return null;
		return stateForPlacement.setValue(AXIS, context.getHorizontalDirection().getClockWise().getAxis());
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.get(state.getValue(AXIS));
    };

    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hitResult) {
        return level.getBlockEntity(pos, SharedBlockEntityTypes.DRYING_RACK.get()).map(rack -> {
            if (!rack.inv.getStackInSlot(0).isEmpty()) {
                rack.dropContents();
                return InteractionResult.SUCCESS;
            } else return InteractionResult.PASS;
        }).orElse(InteractionResult.PASS);
    };

    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        return level.getBlockEntity(pos, SharedBlockEntityTypes.DRYING_RACK.get()).map(rack -> {
            if (!rack.inv.getStackInSlot(0).isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            final ItemStack remainder = rack.inv.insertItem(0, stack, false);
            if (remainder != stack) {
                player.setItemInHand(hand, remainder);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            };
            return ItemInteractionResult.FAIL;
        }).orElse(ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
    };

    @Override
    protected void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) level.getBlockEntity(pos, SharedBlockEntityTypes.DRYING_RACK.get()).ifPresent(DryingRackBlockEntity::dropContents);
        super.onRemove(state, level, pos, newState, movedByPiston);
    };

    @Override
    @Nullable
    public DryingRackBlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new DryingRackBlockEntity(SharedBlockEntityTypes.DRYING_RACK.get(), pos, state);
    };

    @Override
    protected BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) {
        return state.setValue(AXIS, rotation.rotate(Direction.get(AxisDirection.POSITIVE, state.getValue(AXIS))).getAxis());
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.DRYING_RACK;
    };
    
};
