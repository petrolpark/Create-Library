package com.petrolpark.compat.create.common.kinetics.horseMill.ponder;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
public class HarnessWithCowDummyBlock extends HorizontalDirectionalBlock implements IBE<HarnessWithCowDummyBlockEntity> {

    private static final MapCodec<HarnessWithCowDummyBlock> CODEC = simpleCodec(HarnessWithCowDummyBlock::new);

    public HarnessWithCowDummyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    };

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    };

    @Override
    public Class<HarnessWithCowDummyBlockEntity> getBlockEntityClass() {
        return HarnessWithCowDummyBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends HarnessWithCowDummyBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.HARNESS_WITH_COW_DUMMY.get();
    };
    
};
