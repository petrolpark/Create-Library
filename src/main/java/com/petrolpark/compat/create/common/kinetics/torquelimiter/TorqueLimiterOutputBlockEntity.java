package com.petrolpark.compat.create.common.kinetics.torquelimiter;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TorqueLimiterOutputBlockEntity extends GeneratingKineticBlockEntity {

    public TorqueLimiterOutputBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void applyNewSpeed(float prevSpeed, float speed) {
        // TODO Auto-generated method stub
        super.applyNewSpeed(prevSpeed, speed);
    };

    @Override
    public float getGeneratedSpeed() {
        // TODO Auto-generated method stub
        return super.getGeneratedSpeed();
    };
    
};
