package com.petrolpark.core.recipe;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public interface IDummyRecipe extends Recipe<RecipeWrapper> {
    
    @Override
    public default boolean matches(@Nonnull RecipeWrapper input, @Nonnull Level level) {
        return false;
    };

    @Override
    public default ItemStack assemble(@Nonnull RecipeWrapper input, @Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    };

    @Override
    public default boolean canCraftInDimensions(int width, int height) {
        return false;
    };

    @Override
    public default ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    };
};
