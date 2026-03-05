package com.petrolpark.compat.create.common.processing.basinlid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.petrolpark.compat.create.core.CreateShapes;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BasinLidBlock extends HorizontalDirectionalBlock implements IBE<BasinLidBlockEntity>, ProperWaterloggedBlock, ISharedFeature {

    public static final MapCodec<BasinLidBlock> CODEC = simpleCodec(BasinLidBlock::new);

    public static final VoxelShaper SHAPE = CreateShapes.shape(1, 0, 1, 15, 2, 15)
        .add(0, 2, 5.5, 15.5, 16, 10.5)
        .forHorizontal(Direction.NORTH);

    public BasinLidBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
        );
    };

    @Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, FACING);
		super.createBlockStateDefinition(builder);
	};

    @Override
    protected void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        getBlockEntityOptional(level, pos).ifPresent(be -> be.basinChecker.scheduleUpdate());
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return withWater(defaultBlockState().setValue(FACING, context.getHorizontalDirection()), context);
    };

    @Override
    protected BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighborState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        updateWater(level, state, pos);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    };

    @Override
    protected FluidState getFluidState(@Nonnull BlockState state) {
        return fluidState(state);
    };

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    };

    @Override
    public Class<BasinLidBlockEntity> getBlockEntityClass() {
        return BasinLidBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends BasinLidBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.BASIN_LID.get();
    };

    @Override
    protected boolean isPathfindable(@Nonnull BlockState state, @Nonnull PathComputationType pathComputationType) {
        return false;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.BASIN_LID;
    };
    
};
