package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.compat.create.PetrolparkCreateBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.RecipeHolder;

public class JuicingCategory extends BasinCategory {

    private final AnimatedJuicer juicer = new AnimatedJuicer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public JuicingCategory(CreateRecipeCategory.Info<BasinRecipe> info, IJeiHelpers helpers) {
        super(info, true);
    };

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull RecipeHolder<BasinRecipe> holder, @Nonnull IFocusGroup focuses) {
        super.setRecipe(builder, holder, focuses);

        PetrolparkRecipeCategory.addOptionalRequiredBiomeSlot(builder, holder.value(), 3, 3);
        PetrolparkRecipeCategory.addOptionalRecipeBookSlot(getRecipeType(), builder, holder, 22, 3);
    };

    @Override
    public void draw(@Nonnull BasinRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        final HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (requiredHeat != HeatCondition.NONE) heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
			.draw(graphics, getBackground().getWidth() / 2 + 3, 55);
		juicer.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
    };

    class AnimatedJuicer extends AnimatedKinetics {

        @Override
        public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
            final PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(xOffset, yOffset, 200);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
            final int scale = 23;

            blockElement(shaft(Direction.Axis.Z))
                    .rotateBlock(0, 0, getCurrentAngle())
                    .scale(scale)
                    .render(graphics);

            blockElement(AllBlocks.MECHANICAL_PRESS.getDefaultState())
                    .scale(scale)
                    .render(graphics);

            blockElement(AllPartialModels.MECHANICAL_PRESS_HEAD)
                    .atLocal(0, -getAnimatedHeadOffset(), 0)
                    .scale(scale)
                    .render(graphics);

            blockElement(PetrolparkCreateBlocks.MESH_BASIN.getDefaultState())
                    .atLocal(0, 1.65, 0)
                    .scale(scale)
                    .render(graphics);

            matrixStack.popPose();
        }

        private float getAnimatedHeadOffset() {
            float cycle = (AnimationTickHolder.getRenderTime() - offset * 8f) % 30f;
            if (cycle < 10) {
                float progress = cycle / 10;
                return -(progress * progress * progress);
            };
            if (cycle < 15f) return -1f;
            if (cycle < 20f) return -1f + (1f - ((20f - cycle) / 5f));
            return 0;
        };
    };
    
};
