package com.petrolpark.compat.create.common.redstone.programmer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.petrolpark.compat.create.core.CreateShapes;
import com.petrolpark.core.world.block.IPickUpPutDownBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneProgrammerBlock extends HorizontalDirectionalBlock implements IBE<RedstoneProgrammerBlockEntity>, IWrenchable, ProperWaterloggedBlock, IPickUpPutDownBlock, ISharedFeature {

    public static final MapCodec<RedstoneProgrammerBlock> CODEC = HorizontalDirectionalBlock.simpleCodec(RedstoneProgrammerBlock::new);

    public static final VoxelShape SHAPE = CreateShapes.shape(1, 0, 1, 15, 3, 15)
        .add(2, 3, 2, 14, 10, 14)
        .build();

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RedstoneProgrammerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(POWERED, false)
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
        );
    };

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    };

    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            withBlockEntityDo(level, pos, be -> player.openMenu(be.programmer, be.programmer.program.writeToMenu()));
            return InteractionResult.SUCCESS;
        };
        return InteractionResult.PASS;
    };

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        return SHAPE;
    };

    @Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide()) return;
		if (!level.getBlockTicks().willTickThisTick(pos, this)) level.scheduleTick(pos, this, 0);
        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
	};

    @Override
	public void tick(@Nonnull BlockState state, @Nonnull ServerLevel worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource r) {
		updatePower(state, worldIn, pos);
	};

    @Override
	public void onPlace(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
		if (state.getBlock() == oldState.getBlock() || isMoving) return;
		updatePower(state, worldIn, pos);
	};

    public void updatePower(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        if (state.getValue(POWERED) != level.hasNeighborSignal(pos)) level.setBlock(pos, state.cycle(POWERED), 2);
    };

    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighborState, @Nonnull LevelAccessor level, @Nonnull BlockPos currentPos, @Nonnull BlockPos neighborPos) {
        updateWater(level, state, currentPos);
        return state;
    };

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder
            .add(POWERED)
            .add(FACING)
            .add(WATERLOGGED);
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return withWater(defaultBlockState().setValue(FACING, context.getHorizontalDirection()), context);
    };

    @Override
    public FluidState getFluidState(@Nonnull BlockState state) {
        return fluidState(state);
    };

    @Override
    protected boolean isPathfindable(@Nonnull BlockState state, @Nonnull PathComputationType pathComputationType) {
        return false;
    };

    @Override
    public Class<RedstoneProgrammerBlockEntity> getBlockEntityClass() {
        return RedstoneProgrammerBlockEntity.class;
    };

    @Override
    public BlockEntityType<RedstoneProgrammerBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.REDSTONE_PROGRAMMER.get();
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.REDSTONE_PROGRAMMER;
    };
    
};
