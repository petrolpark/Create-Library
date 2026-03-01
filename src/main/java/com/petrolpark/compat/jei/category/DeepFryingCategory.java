package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.compat.create.CreateBlocks;
import com.petrolpark.compat.create.common.processing.meshbasin.DeepFryingRecipe;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.client.gui.GuiGraphics;

public class DeepFryingCategory extends SmallBasinCategory<DeepFryingRecipe> {

    private final AnimatedMeshBasin basin = new AnimatedMeshBasin();

    public DeepFryingCategory(Info<DeepFryingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    protected void draw(@Nonnull DeepFryingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        basin.draw(graphics, getBackground().getWidth() / 2 + 3, 49);
    };

    class AnimatedMeshBasin extends AnimatedKinetics {

        @Override
        public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(xOffset, yOffset, 200);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
            int scale = 23;

            blockElement(CreateBlocks.MESH_BASIN.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

            matrixStack.popPose();;
        };

    };
    
};
