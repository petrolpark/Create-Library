package com.petrolpark.compat.create.common.processing.mandrel;

import javax.annotation.Nonnull;

import com.petrolpark.compat.create.CreateBlockEntityTypes;
import com.petrolpark.compat.create.core.CreateShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MandrelBlock extends HorizontalKineticBlock implements IBE<MandrelBlockEntity> {

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
    
};
