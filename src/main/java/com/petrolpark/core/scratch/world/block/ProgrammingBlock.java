package com.petrolpark.core.scratch.world.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.core.block.OrientedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ProgrammingBlock extends OrientedBlock implements EntityBlock {

    public ProgrammingBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newBlockEntity'");
    };
    
};
