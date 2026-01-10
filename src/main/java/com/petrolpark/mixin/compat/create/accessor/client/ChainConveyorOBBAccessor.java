package com.petrolpark.mixin.compat.create.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape.ChainConveyorOBB;

import net.minecraft.core.BlockPos;

@Mixin(ChainConveyorOBB.class)
public interface ChainConveyorOBBAccessor {
     
    @Accessor("connection")
    public BlockPos getConnection();
};
