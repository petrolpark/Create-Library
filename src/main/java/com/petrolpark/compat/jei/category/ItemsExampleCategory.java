package com.petrolpark.compat.jei.category;

import java.util.List;

import com.petrolpark.core.recipe.ExampleRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ItemsExampleCategory extends SimpleConversionCategory<ExampleRecipe> {
    
    public ItemsExampleCategory(CreateRecipeCategory.Info<ExampleRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public Ingredient getInput(ExampleRecipe recipe, IFocusGroup focuses) {
        return recipe.ingredient().left().orElse(Ingredient.EMPTY);
    };

    @Override
    public List<ItemStack> getOutputs(ExampleRecipe recipe, IFocusGroup focuses) {
        return recipe.result().left().stream().toList();
    };
};
