package com.petrolpark.compat.create.core.dough;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.petrolpark.compat.create.core.dough.rollingPin.IRollableBlock;
import com.petrolpark.core.world.block.IPickUpPutDownBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoughBlock extends Block implements IBE<DoughBlockEntity>, IRollableBlock, IPickUpPutDownBlock {

    public DoughBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return getBlockEntityOptional(level, pos).flatMap(DoughBlockEntity::getVoxelShape).orElse(Shapes.empty());
    };

    @Override
    public ItemStack getCloneItemStack(@Nonnull BlockState state, @Nonnull HitResult target, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        final ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        withBlockEntityDo(level, pos, be -> stack.applyComponents(be.collectComponents()));
        return stack;
    };

    @SuppressWarnings("null")
    public static final int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        return level == null && pos == null ? -1 : level.getBlockEntity(pos, PetrolparkCreateBlockEntityTypes.DOUGH.get()).map(be -> be.doughData.dough().tint()).orElse(-1);
    };

    @Override
    public boolean canBeRollingPinRolled(Level level, BlockPos pos, Direction horizontalLookingDirection) {
        return getBlockEntityOptional(level, pos).map(be -> be.doughData.isRollable(horizontalLookingDirection.getAxis() == Axis.Z)).orElse(false);
    };

    @Override
    public void rollingPinRoll(Level level, BlockPos pos, Direction horizontalLookingDirection, boolean byPlayer) {
        final boolean lengthwise = horizontalLookingDirection.getAxis() == Axis.Z;
        withBlockEntityDo(level, pos, be -> be.modifyDough(dough -> dough.isRollable(lengthwise) ? dough.rolled(lengthwise, byPlayer) : dough));
    };

    @Override
    public Class<DoughBlockEntity> getBlockEntityClass() {
        return DoughBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends DoughBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.DOUGH.get();
    };
    
};
