package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateBlocks;
import com.petrolpark.compat.create.common.processing.basinlid.LiddedBasinRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.client.gui.GuiGraphics;

public class LiddedBasinCategory extends SmallBasinCategory<LiddedBasinRecipe> implements ISharedFeature {

    private final AnimatedLiddedBasin basin = new AnimatedLiddedBasin();

    public LiddedBasinCategory(CreateRecipeCategory.Info<LiddedBasinRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void draw(@Nonnull LiddedBasinRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        basin.draw(graphics, getBackground().getWidth() / 2 + 3, 49);
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

            blockElement(PetrolparkCreateBlocks.BASIN_LID.getDefaultState())
                .atLocal(0, -1, 0)
                .scale(scale)
                .render(graphics);

            matrixStack.popPose();
        };

    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.BASIN_LID;
    };
    
};
