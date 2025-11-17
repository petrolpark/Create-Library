package com.petrolpark.core.scratch.world.block;

import com.petrolpark.core.block.entity.BlockEntityBase;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ProgrammingBlockEntity extends BlockEntityBase {

    public ProgrammingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    };
    
};
