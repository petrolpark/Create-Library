package com.petrolpark.compat.create.common.kinetics.torquelimiter;

import java.util.Optional;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TorqueLimiterInputBlock extends DirectionalKineticBlock implements IBE<TorqueLimiterInputBlockEntity> {

    public TorqueLimiterInputBlock(Properties properties) {
        super(properties);
    };

    public static final Optional<TorqueLimiterOutputBlockEntity> getOutput(LevelReader world, BlockPos pos, BlockState state) {
        return world.getBlockEntity(pos.relative(state.getValue(FACING)), null);
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    };
    
    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    };

    @Override
    public Class<TorqueLimiterInputBlockEntity> getBlockEntityClass() {
        return TorqueLimiterInputBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends TorqueLimiterInputBlockEntity> getBlockEntityType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockEntityType'");
    };
    
};
