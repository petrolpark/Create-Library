package com.petrolpark.core.recipe.book;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRecipeBookAcceptorBlock {
    
    default void addProxyRecipeBookAcceptorPositions(Level level, BlockPos pos, BlockState state, Consumer<BlockPos> posAdder) {};

    public boolean acceptsRecipeBook(Level level, BlockPos pos, BlockState state, RecipeHolder<?> recipeHolder);
};
