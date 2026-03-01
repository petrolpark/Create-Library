package com.petrolpark.compat.create.common.processing.mandrel;

import javax.annotation.Nonnull;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.CreateBlockEntityTypes;
import com.petrolpark.compat.create.core.CreateShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class MandrelBlock extends HorizontalKineticBlock implements IBE<MandrelBlockEntity>, ISharedFeature {

    public static final VoxelShaper SHAPE = CreateShapes.shape(0d, 0d, 0d, 16d, 5d, 16d)
        .add(0d, 5d, 12d, 16d, 16d, 16d)
        .add(5d, 5d, 1d, 11d, 11d, 12d)
        .forHorizontal(Direction.NORTH);

    public MandrelBlock(Properties properties) {
        super(properties);
    };
    

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.get(state.getValue(HORIZONTAL_FACING));
    };

    /*
     * Temporary, copied from Millstone
     */
    @Override
	protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
		if (!stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

		withBlockEntityDo(level, pos, mandrel -> {
			boolean emptyOutput = true;
			IItemHandlerModifiable inv = mandrel.outputInv;
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (!stackInSlot.isEmpty())
					emptyOutput = false;
				player.getInventory()
					.placeItemBackInInventory(stackInSlot);
				inv.setStackInSlot(slot, ItemStack.EMPTY);
			}

			if (emptyOutput) {
				inv = mandrel.inputInv;
				for (int slot = 0; slot < inv.getSlots(); slot++) {
					player.getInventory()
						.placeItemBackInInventory(inv.getStackInSlot(slot));
					inv.setStackInSlot(slot, ItemStack.EMPTY);
				}
			}

			mandrel.setChanged();
			mandrel.sendData();
		});

		return ItemInteractionResult.SUCCESS;
	};

    /*
     * Temporary, copied from Millstone
     */
    @Override
	public void updateEntityAfterFallOn(@Nonnull BlockGetter worldIn, @Nonnull Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);

		//if (entityIn.level().isClientSide()) return;
		if (!(entityIn instanceof ItemEntity itemEntity)) return;
		if (!entityIn.isAlive()) return;

		MandrelBlockEntity mandrel = null;
		for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition())) if (mandrel == null) mandrel = getBlockEntity(worldIn, pos);

		if (mandrel == null) return;

		IItemHandler capability = entityIn.level().getCapability(Capabilities.ItemHandler.BLOCK, mandrel.getBlockPos(), Direction.UP);
		if (capability == null) return;

		ItemStack remainder = capability.insertItem(0, itemEntity.getItem(), false);
		if (remainder.isEmpty()) itemEntity.discard();
		if (remainder.getCount() < itemEntity.getItem().getCount()) itemEntity.setItem(remainder);
	};

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING).getOpposite();
    };

    @Override
    public Class<MandrelBlockEntity> getBlockEntityClass() {
        return MandrelBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends MandrelBlockEntity> getBlockEntityType() {
        return CreateBlockEntityTypes.MANDREL.get();
    };

	@Override
	public SharedFeatureFlag getSharedFeatureFlag() {
		return SharedFeatureFlag.MANDREL;
	};
    
};
