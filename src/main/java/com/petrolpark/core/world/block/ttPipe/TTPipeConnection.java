package com.petrolpark.core.world.block.ttPipe;

import javax.annotation.Nullable;

import com.petrolpark.core.world.block.ttPipe.valve.IValve;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

/**
 * @param relativePos The relative position of the pipe to connect to
 * @param face The face of the Block of the pipe to connect to
 */
public record TTPipeConnection<VALVE extends IValve<VALVE>>(Vec3i relativePos, Direction face, @Nullable VALVE valve) {
    
};
