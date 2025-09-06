package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.recipe.book.IRecipeBookAcceptorBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CraftingTableBlock.class)
public abstract class CraftingTableBlockMixin extends Block implements IRecipeBookAcceptorBlock {

    public CraftingTableBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
        throw new AssertionError();
    };

    @Override
    public boolean acceptsRecipeBook(Level level, BlockPos pos, BlockState state, RecipeHolder<?> recipeHolder) {
        return recipeHolder.value().getType() == PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get();
    };
    
};
