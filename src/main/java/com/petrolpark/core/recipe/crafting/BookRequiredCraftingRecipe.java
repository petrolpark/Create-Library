package com.petrolpark.core.recipe.crafting;

import java.util.Collections;

import com.petrolpark.PetrolparkRecipeSerializers;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.recipe.INamedRecipe;
import com.petrolpark.core.recipe.book.IBookRequiredRecipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class BookRequiredCraftingRecipe extends WrappedCraftingRecipe implements IBookRequiredRecipe {

    protected Component name = null;

    public BookRequiredCraftingRecipe(CraftingRecipe wrappedRecipe) {
        super(wrappedRecipe);
    };

    protected void setName(Component name) {
        this.name = name;
    };

    @Override
    @SuppressWarnings("null")
    public Component getName(ResourceLocation recipeId) {
        return INamedRecipe.cacheDefaultName(name, this::setName, recipeId, () -> Collections.singletonList(getResultItem(null).getHoverName()));
    };

    @Override
    public RecipeType<BookRequiredCraftingRecipe> getType() {
        return PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get();
    };

    @Override
    public RecipeSerializer<BookRequiredCraftingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.CRAFTING_BOOK_REQUIRED.get();
    };

    @Override
    public boolean isBookRequired(Level level) {
        return true;
    };

    
};
