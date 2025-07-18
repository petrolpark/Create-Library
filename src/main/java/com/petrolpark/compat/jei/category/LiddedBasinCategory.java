package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.CreateBlocks;
import com.petrolpark.compat.jei.JEIBlockRenderer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.client.gui.GuiGraphics;

public class LiddedBasinCategory extends BasinCategory {

    private final JEIBlockRenderer blockRenderer = new JEIBlockRenderer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public LiddedBasinCategory(Info<BasinRecipe> info, IJeiHelpers helpers) {
        super(info, true);
    };

    @Override
    public void draw(@Nonnull BasinRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        PoseStack ms = graphics.pose();

        HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (requiredHeat != HeatCondition.NONE) heater.withHeat(requiredHeat.visualizeAsBlazeBurner()).draw(graphics, getBackground().getWidth() / 2 + 3, 55);
        ms.pushPose();
        ms.translate(getBackground().getWidth() / 2 + 3, 71, 0);
        blockRenderer.renderBlock(AllBlocks.BASIN.getDefaultState(), graphics, 23f);
        ms.translate(0, -21, 0);
		blockRenderer.renderBlock(CreateBlocks.BASIN_LID.getDefaultState(), graphics, 23f);
        ms.popPose();
    };
    
};
