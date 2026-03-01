package com.petrolpark.compat.create.common.processing.blender;

import javax.annotation.Nonnull;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlenderBlock extends KineticBlock implements IBE<BlenderBlockEntity>, ICogWheel, ISharedFeature {

    public static final VoxelShape SHAPE = new AllShapes.Builder(Block.box(0d, 0d, 0d, 16d, 5d, 16d))
        .add(2d, 5d, 2d, 14d, 11d, 14d)
        .add(0d, 11d, 0d, 16d, 13d, 16d)
        .add(2d, 13d, 2d, 14d, 16d, 14d)
        .build();

    public BlenderBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    };

    @Override
    public void updateEntityAfterFallOn(@Nonnull BlockGetter level, @Nonnull Entity entity) {
        super.updateEntityAfterFallOn(level, entity);
        if (entity instanceof LivingEntity livingEntity) withBlockEntityDo(level, entity.getOnPos(), be -> be.addHurtingEntity(livingEntity));
    };

    @Override
    protected BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighborState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (direction == Direction.UP) withBlockEntityDo(level, pos, BlenderBlockEntity::notifyUpdate); // Update Flywheel Visual
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    };

    @Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.FAST;
	};

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    };

    @Override
    public Class<BlenderBlockEntity> getBlockEntityClass() {
        return BlenderBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends BlenderBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.BLENDER.get();
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.BLENDER;
    };
    
};
