package com.petrolpark.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Either;
import com.petrolpark.Petrolpark;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateBlocks;
import com.petrolpark.compat.create.PetrolparkCreateFluids;
import com.petrolpark.core.recipe.ExampleRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class MysteriousConversionCategory extends PetrolparkRecipeCategory<ExampleRecipe> {

    public static final List<RecipeHolder<ExampleRecipe>> RECIPES = new ArrayList<>();

	static {
		if (SharedFeatureFlag.BLENDER.enabled() && SharedFeatureFlag.BLOOD.enabled()) RECIPES.add(new RecipeHolder<>(Petrolpark.asResource("blood_from_blender"), new ExampleRecipe(Either.left(Ingredient.of(PetrolparkCreateBlocks.BLENDER)), Either.right(new FluidStack(PetrolparkCreateFluids.BLOOD.get().getSource(), 250)))));
    };

    public MysteriousConversionCategory(CreateRecipeCategory.Info<ExampleRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    protected void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull ExampleRecipe recipe, @Nonnull IFocusGroup focuses) {
        
        recipe.ingredient()
            .ifLeft(ingredient -> builder
                .addSlot(RecipeIngredientRole.INPUT, 27, 17)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(ingredient)
            ).ifRight(ingredient -> addFluidSlot(builder, 27, 17, ingredient));

        recipe.result()
            .ifLeft(result -> builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 17)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(result)
            ).ifRight(result -> addFluidSlot(builder, 132, 17, result));
    };

    @Override
    protected void draw(@Nonnull ExampleRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 20);
		AllGuiTextures.JEI_QUESTION_MARK.render(graphics, 77, 5);
    };
  
    
};