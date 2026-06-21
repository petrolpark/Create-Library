package petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;

public class HarnessBlock extends HorizontalDirectionalBlock implements ISharedFeature {

    public static final MapCodec<HarnessBlock> CODEC = simpleCodec(HarnessBlock::new);

    public HarnessBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    };

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    };
    
    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return AllShapes.CASING_2PX.get(state.getValue(FACING));
    };

    @Override
	protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
		if (player.isShiftKeyDown() || player instanceof FakePlayer) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		final List<HarnessEntity> harnesses = level.getEntitiesOfClass(HarnessEntity.class, new AABB(pos));
		if (!harnesses.isEmpty()) {
			if (!level.isClientSide()) harnesses.get(0).ejectPassengers();
			return ItemInteractionResult.SUCCESS;
		};

		if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
		final Optional<Entity> leashed = SeatBlock.getLeashed(level, player).toJavaUtil().filter(HarnessBlock::canBeHarnessed); // Why the hell is this a Guava Optional
        if (leashed.isPresent()) harness(level, pos, leashed.get());
		return ItemInteractionResult.SUCCESS;
	};

    public static final boolean canBeHarnessed(Entity entity) {
        return HorseMillProperties.get(entity).isPresent();
    };

    public static final boolean isHarnessOccupied(Level world, BlockPos pos) {
		return !world.getEntitiesOfClass(HarnessEntity.class, new AABB(pos)).isEmpty();
	};

    public static final void harness(Level level, BlockPos pos, Entity entity) {
		final HarnessEntity harness = new HarnessEntity(level);
		harness.setPos(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
		level.addFreshEntity(harness);
		entity.startRiding(harness, true);
	};
    
    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.HORSE_MILL;
    };
    
};
