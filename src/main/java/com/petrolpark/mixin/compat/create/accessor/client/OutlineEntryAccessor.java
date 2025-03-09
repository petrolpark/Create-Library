package com.petrolpark.mixin.compat.create.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.createmod.catnip.outliner.Outliner.OutlineEntry;

@Mixin(value = OutlineEntry.class, remap = false)
public interface OutlineEntryAccessor {
    
    @Accessor("ticksTillRemoval")
    public void setTicksTillRemoval(int ticksTillRemoval);
};
