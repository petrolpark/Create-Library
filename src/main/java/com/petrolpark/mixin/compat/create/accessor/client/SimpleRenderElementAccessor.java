package com.petrolpark.mixin.compat.create.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.gui.element.AbstractRenderElement.SimpleRenderElement;

@Mixin(SimpleRenderElement.class)
public interface SimpleRenderElementAccessor {
    
    @Accessor(
        value = "renderable",
        remap = false
    )
    public ScreenElement getRenderable();
};
