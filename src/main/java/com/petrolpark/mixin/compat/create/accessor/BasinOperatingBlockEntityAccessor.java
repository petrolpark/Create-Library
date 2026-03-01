package com.petrolpark.mixin.compat.create.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

@Mixin(BasinOperatingBlockEntity.class)
public interface BasinOperatingBlockEntityAccessor {
    
    @Invoker(
        remap = false
    )
    public boolean callMatchStaticFilters(RecipeHolder<? extends Recipe<?>> recipeHolder);
};
