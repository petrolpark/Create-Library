package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.create.common.processing.centrifuge.ICentrifugationRecipe;
import com.petrolpark.core.recipe.IBiomeSpecificRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class CentrifugationCategory<R extends Recipe<?> & ICentrifugationRecipe> extends PetrolparkRecipeCategory<R> {

    private static final AnimatedCentrifuge centrifuge = new AnimatedCentrifuge();

    private static final int CENTRIFUGE_X = 35;
    private static final int CENTRIFUGE_Y = 60;

    public CentrifugationCategory(CreateRecipeCategory.Info<R> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull R recipe, @Nonnull IFocusGroup focuses) {

        final SizedFluidIngredient inputFluid = recipe.getFluidIngredients().iterator().next();
        FluidStack denseOutputFluid = recipe.getDenseOutputFluid();
        FluidStack lightOutputFluid = recipe.getLightOutputFluid();

        addFluidSlot(builder, 3, 3, inputFluid);
        if (recipe instanceof IBiomeSpecificRecipe biomeRecipe) addOptionalRequiredBiomeSlot(builder, biomeRecipe, 3, 19);

        addFluidSlot(builder, 99, 38, denseOutputFluid);
        addFluidSlot(builder, 33, 96, lightOutputFluid);
    };

    @Override
    public void draw(@Nonnull R recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {

        AllGuiTextures.JEI_SHADOW.render(graphics, CENTRIFUGE_X - 19, CENTRIFUGE_Y - 5);
        centrifuge.draw(graphics, CENTRIFUGE_X, CENTRIFUGE_Y);

        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 29, 9);
        PetrolparkGuiTexture.JEI_SHORT_DOWN_ARROW.render(graphics, 33, 70);
        PetrolparkGuiTexture.JEI_SHORT_RIGHT_ARROW.render(graphics, 72, 38);
    };
    
};
