package com.petrolpark.mixin.compat.create.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.createmod.catnip.outliner.Outline.OutlineParams;

@Mixin(value = OutlineParams.class, remap = false)
public interface OutlineParamsAccessor {
    
    @Accessor("lightmap")
    public int getLightmap();

    @Accessor("disableLineNormals")
    public boolean getDisableLineNormals();
};
