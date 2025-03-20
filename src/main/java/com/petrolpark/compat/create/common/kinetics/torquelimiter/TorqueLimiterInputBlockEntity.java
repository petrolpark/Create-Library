package com.petrolpark.compat.create.common.kinetics.torquelimiter;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TorqueLimiterInputBlockEntity extends KineticBlockEntity {

    public TorqueLimiterInputBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };
    
};
