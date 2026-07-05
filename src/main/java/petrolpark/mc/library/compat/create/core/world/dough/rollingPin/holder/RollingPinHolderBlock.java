package petrolpark.mc.library.compat.create.core.world.dough.rollingPin.holder;

import javax.annotation.Nonnull;

import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateItems;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class RollingPinHolderBlock extends HorizontalAxisKineticBlock implements IBE<RollingPinHolderBlockEntity>, ICogWheel, ISharedFeature {

    public static final VoxelShape SHAPE = Block.box(0d, 2d, 0d, 16d, 16d, 16d);

    public RollingPinHolderBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HORIZONTAL_AXIS, context.getHorizontalDirection().getAxis());
    };

    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        return onBlockEntityUseItemOn(level, pos, be -> {
            if (!be.rollingPin.isEmpty() || !SharedCreateItems.ROLLING_PIN.isIn(stack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            be.rollingPin = stack.copyWithCount(1);
            stack.shrink(1);
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
            return ItemInteractionResult.SUCCESS;
        });
    };

    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hitResult) {
        return onBlockEntityUse(level, pos, be -> {
            if (be.rollingPin.isEmpty() || !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) return InteractionResult.PASS;
            player.getInventory().placeItemBackInInventory(be.rollingPin);
            be.rollingPin = ItemStack.EMPTY;
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
            return InteractionResult.SUCCESS;
        });
    };

    @Override
    public boolean onDestroyedByPlayer(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, boolean willHarvest, @Nonnull FluidState fluid) {
        if (player.hasInfiniteMaterials()) withBlockEntityDo(level, pos, RollingPinHolderBlockEntity::clearContent);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    };

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        final Player player = context.getPlayer();
        if (player != null && !player.hasInfiniteMaterials()) withBlockEntityDo(context.getLevel(), context.getClickedPos(), be -> {
            player.getInventory().placeItemBackInInventory(be.rollingPin);
            be.rollingPin = ItemStack.EMPTY;
        });
        return super.onSneakWrenched(state, context);
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_AXIS) == Axis.X ? Axis.Z : Axis.X;
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return false;
    };

    @Override
    public Class<RollingPinHolderBlockEntity> getBlockEntityClass() {
        return RollingPinHolderBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends RollingPinHolderBlockEntity> getBlockEntityType() {
        return SharedCreateBlockEntityTypes.ROLLING_PIN_HOLDER.get();
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.ROLLING_PIN;
    };
    
};
