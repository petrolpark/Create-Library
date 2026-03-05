package com.petrolpark.core.recipe;

import com.petrolpark.compat.SharedFeatureFlag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public record SharedRecipeType<R extends Recipe<?>>(ResourceLocation id, SharedFeatureFlag featureFlag) implements RecipeType<R> {
    
    @Override
    public final String toString() {
        return id.toString();
    };
};
