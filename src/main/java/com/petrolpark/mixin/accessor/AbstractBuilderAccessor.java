package com.petrolpark.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.tterrag.registrate.builders.AbstractBuilder;

@Mixin(AbstractBuilder.class)
public interface AbstractBuilderAccessor {
    
    @Accessor("isOptional")
    public boolean getIsOptional();
};
