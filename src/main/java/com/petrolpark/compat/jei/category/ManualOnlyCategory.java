package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.recipe.manualonly.ManualOnlyShapedRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

public class ManualOnlyCategory extends PetrolparkRecipeCategory<CraftingRecipe> {

    public ManualOnlyCategory(Info<CraftingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CraftingRecipe craftingRecipe, @Nonnull IFocusGroup focuses) {
        if (!(craftingRecipe instanceof ManualOnlyShapedRecipe recipe)) return;
        int gridSize = recipe.getWidth() <= 2 && recipe.getHeight() <= 2 ? 2 : 3;
        for (int i = 0; i < (gridSize == 2 ? 4 : 9); i++) {
            int x = i % gridSize;
            int y = i / gridSize;
            Ingredient ingredient = (x >= recipe.getWidth() || y >= recipe.getHeight()) ? Ingredient.EMPTY : recipe.getIngredients().get(y * recipe.getWidth() + x);
            builder.addSlot(RecipeIngredientRole.INPUT, (gridSize == 2 ? 10 : 1) + x * 19, (gridSize == 2 ? 10 : 1) + y * 19)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(ingredient);
        };
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(recipe.getExampleResult(getRegistryAccess()));
    };

    @Override
    public void draw(@Nonnull CraftingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        PetrolparkGuiTexture.JEI_POINTING_HAND.render(guiGraphics, 70, 21);
    };
    
};
