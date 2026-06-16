package com.petrolpark.compat.create.common.kinetics.horseMill.ponder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HarnessWithCowDummyBlockEntity extends BlockEntity {

    public boolean walking = false;

    public HarnessWithCowDummyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    };
    
};
