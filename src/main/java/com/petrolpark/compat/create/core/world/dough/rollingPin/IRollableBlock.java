package com.petrolpark.compat.create.core.world.dough.rollingPin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IRollableBlock {
    
    public boolean canBeRollingPinRolled(Level level, BlockPos pos, Direction horizontalLookingDirection);

    public void rollingPinRoll(Level level, BlockPos pos, Direction horizontalLookingDirection, boolean byPlayer);
};
