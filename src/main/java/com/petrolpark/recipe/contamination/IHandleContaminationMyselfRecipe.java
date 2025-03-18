package com.petrolpark.recipe.contamination;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public interface IHandleContaminationMyselfRecipe<I extends RecipeInput> extends Recipe<I> {
    
    public default boolean isContaminationHandled(I input, HolderLookup.Provider registries) {
        return true;
    };
};
