package com.petrolpark.compat.create.common.kinetics.torquelimiter;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TorqueLimiterOutputBlock extends DirectionalKineticBlock implements IBE<TorqueLimiterOutputBlockEntity> {

    public TorqueLimiterOutputBlock(Properties properties) {
        super(properties);
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    };
    
    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    };

    @Override
    public Class<TorqueLimiterOutputBlockEntity> getBlockEntityClass() {
        return TorqueLimiterOutputBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends TorqueLimiterOutputBlockEntity> getBlockEntityType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockEntityType'");
    };
    
};
