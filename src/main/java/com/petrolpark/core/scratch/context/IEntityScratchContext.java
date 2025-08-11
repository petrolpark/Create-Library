package com.petrolpark.core.scratch.context;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface IEntityScratchContext extends ILevelScratchContext, IAtPosScratchContext {
    
    public Entity entity();

    @ApiStatus.NonExtendable
    @Override
    public default Level level() {
        return entity().level();
    };

    @ApiStatus.NonExtendable
    @Override
    public default BlockPos blockPos() {
        return entity().getOnPos();
    };
};
