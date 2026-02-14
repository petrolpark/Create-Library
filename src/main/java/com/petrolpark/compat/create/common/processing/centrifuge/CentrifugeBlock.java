package com.petrolpark.compat.create.common.processing.centrifuge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.compat.create.CreateBlockEntityTypes;
import com.petrolpark.compat.create.core.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CentrifugeBlock extends KineticBlock implements IBE<CentrifugeBlockEntity>, ICogWheel, ProperWaterloggedBlock {

    public static final VoxelShape SHAPE = new AllShapes.Builder(Block.box(0, 0, 0, 16, 4, 16))
        .add(2, 4, 2, 14, 12, 14)
        .add(0, 12, 0, 16, 16, 16)
        .build();

    public CentrifugeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ProperWaterloggedBlock.WATERLOGGED, false));
    };

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, ProperWaterloggedBlock.WATERLOGGED);
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return withWater(blockStateWithConnections(context.getLevel(), context.getClickedPos(), defaultBlockState()), context);
    };

    @Override
    protected FluidState getFluidState(@Nonnull BlockState state) {
        return fluidState(state);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    };

    @Override
    protected BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighborState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        updateWater(level, state, pos);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos).setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), FluidPipeBlock.canConnectTo(level, neighborPos, neighborState, direction));
    };

    @Override
    @SuppressWarnings("null")
    public void onNeighborChange(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockPos neighbor) {
        withBlockEntityDo(level, pos, be -> be.getLevel().setBlockAndUpdate(pos, blockStateWithConnections(level, pos, state)));
    };

    public BlockState blockStateWithConnections(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        for (Direction direction : Iterate.horizontalDirections) {
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), FluidPipeBlock.canConnectTo(level, pos.relative(direction), level.getBlockState(pos.relative(direction)), direction));
        };
        return state;
    };

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        AbstractRememberPlacerBehaviour.setPlacedBy(worldIn, pos, placer);
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    };

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pState);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    };

    @Override
    public BlockEntityType<CentrifugeBlockEntity> getBlockEntityType() {
        return CreateBlockEntityTypes.CENTRIFUGE.get();
    };

    @Override
    public Class<CentrifugeBlockEntity> getBlockEntityClass() {
        return CentrifugeBlockEntity.class;
    };
    
};
