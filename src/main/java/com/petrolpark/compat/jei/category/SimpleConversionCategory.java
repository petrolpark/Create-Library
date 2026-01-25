package com.petrolpark.compat.jei.category;

import java.util.List;

import javax.annotation.Nonnull;

import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public abstract class SimpleConversionCategory<R extends Recipe<?>> extends PetrolparkRecipeCategory<R> {

    public SimpleConversionCategory(Info<R> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    public abstract Ingredient getInput(R recipe, IFocusGroup focuses);

    public abstract List<ItemStack> getOutputs(R recipe, IFocusGroup focuses);

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull R recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(getInput(recipe, focuses));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStacks(getOutputs(recipe, focuses));
    };

    @Override
    public void draw(@Nonnull R recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 27, 6);
    };
    
};
