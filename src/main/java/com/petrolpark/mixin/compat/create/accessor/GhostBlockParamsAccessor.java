package com.petrolpark.mixin.compat.create.accessor;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.createmod.catnip.ghostblock.GhostBlockParams;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(GhostBlockParams.class)
public interface GhostBlockParamsAccessor {
    
    @Accessor("state")
    public BlockState getState();

    @Accessor("pos")
    public BlockPos getPos();

    @Accessor("alphaSupplier")
    public Supplier<Float> getAlphaSupplier();
};
