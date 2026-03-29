package com.petrolpark.compat.create.common.kinetics.horseMill;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Optional;
import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateDataMapTypes;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
	protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
		if (player.isShiftKeyDown() || player instanceof FakePlayer) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		final List<HarnessEntity> harnesses = level.getEntitiesOfClass(HarnessEntity.class, new AABB(pos));
		if (!harnesses.isEmpty()) {
			if (!level.isClientSide()) harnesses.get(0).ejectPassengers();
			return ItemInteractionResult.SUCCESS;
		};

		if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
		final Optional<Entity> leashed = SeatBlock.getLeashed(level, player); // Why the hell is this a Guava Optional
        if (leashed.isPresent()) harness(level, pos, leashed.get());
		return ItemInteractionResult.SUCCESS;
	};

    @SuppressWarnings("deprecation")
    public static final boolean canBeHarnessed(Entity entity) {
        return entity.getType().builtInRegistryHolder().getData(PetrolparkCreateDataMapTypes.HORSE_MILL_PROPERTIES) != null;
    };

    public static final boolean isHarnessOccupied(Level world, BlockPos pos) {
		return !world.getEntitiesOfClass(HarnessEntity.class, new AABB(pos)).isEmpty();
	};

    public static final void harness(Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide()) return;
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
