package com.petrolpark.mixin.compat.create.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.contraptions.Contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

@Mixin(Contraption.class)
public interface ContraptionAccessor {
    
    @Accessor("initialPassengers")
    public Map<BlockPos, Entity> getInitialPassengers();
};
