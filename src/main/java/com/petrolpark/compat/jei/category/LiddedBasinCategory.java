package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.compat.create.CreateBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.client.gui.GuiGraphics;

//TODO Biome-specificity
public class LiddedBasinCategory extends BasinCategory {

    private final AnimatedLiddedBasin basin = new AnimatedLiddedBasin();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public LiddedBasinCategory(Info<BasinRecipe> info, IJeiHelpers helpers) {
        super(info, true);
    };

    @Override
    public void draw(@Nonnull BasinRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        final HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (requiredHeat != HeatCondition.NONE) heater.withHeat(requiredHeat.visualizeAsBlazeBurner()).draw(graphics, getBackground().getWidth() / 2 + 3, 55);
        basin.draw(graphics, getBackground().getWidth() / 2 + 3, 71);
    };

    class AnimatedLiddedBasin extends AnimatedKinetics {

        @Override
        public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(xOffset, yOffset, 200);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
            int scale = 23;

            blockElement(AllBlocks.BASIN.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

            blockElement(CreateBlocks.BASIN_LID.getDefaultState())
                .atLocal(0, -1, 0)
                .scale(scale)
                .render(graphics);

            matrixStack.popPose();
        };

    };
    
};
