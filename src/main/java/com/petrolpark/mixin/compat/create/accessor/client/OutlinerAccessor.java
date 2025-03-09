package com.petrolpark.mixin.compat.create.accessor.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.outliner.Outliner.OutlineEntry;

@Mixin(value = Outliner.class, remap = false)
public interface OutlinerAccessor {
    
    @Accessor("outlines")
    public Map<Object, OutlineEntry> getOutlines();

};
