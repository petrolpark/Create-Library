package com.petrolpark.compat.create.shared.content.processing.meshBasin;

import com.petrolpark.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import com.petrolpark.shared.ISharedFeature;
import com.petrolpark.shared.SharedFeatureFlag;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.processing.basin.BasinBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MeshBasinBlock extends BasinBlock implements ISharedFeature {

    public static final VoxelShape SHAPE = new AllShapes.Builder(Block.box(0d, 2d, 0d, 16d, 16d, 16d))
        .erase(2d, 8d, 2d, 14d, 16d, 14d)
        .add(2, 0, 2, 14, 2, 14)
        .build();

    public MeshBasinBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    };

    @Override
    public BlockEntityType<MeshBasinBlockEntity> getBlockEntityType() {
        return SharedCreateBlockEntityTypes.MESH_BASIN.get();
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.MESH_BASIN;
    };
    
};
