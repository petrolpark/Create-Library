package com.petrolpark.compat.create.common.kinetics.horseMill;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft
.world.level.block.state.properties.DirectionProperty;

public class HorseMillBearingBlock extends KineticBlock implements IBE<HorseMillBearingBlockEntity>, ISharedFeature {

    public static final DirectionProperty FACING = BlockStateProperties.VERTICAL_DIRECTION;

    public HorseMillBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingVerticalDirection().getOpposite());
    };

    @Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING).getOpposite();
	};

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
	public boolean showCapacityWithAnnotation() {
		return true;
	};

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		InteractionResult resultType = super.onWrenched(state, context);
		if (!context.getLevel().isClientSide() && resultType.consumesAction()) context.getLevel().getBlockEntity(context.getClickedPos(), PetrolparkCreateBlockEntityTypes.HORSE_MILL_BEARING.get()).ifPresent(HorseMillBearingBlockEntity::disassemble);
		return resultType;
	};

    @Override
    public Class<HorseMillBearingBlockEntity> getBlockEntityClass() {
        return HorseMillBearingBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends HorseMillBearingBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.HORSE_MILL_BEARING.get();
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.HORSE_MILL;
    };
    
};
