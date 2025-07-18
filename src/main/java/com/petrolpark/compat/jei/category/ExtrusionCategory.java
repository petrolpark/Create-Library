package com.petrolpark.compat.jei.category;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.CreateBlocks;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionRecipe;
import com.petrolpark.compat.jei.JEIBlockRenderer;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@RequiresCreate
public class ExtrusionCategory extends PetrolparkRecipeCategory<ExtrusionRecipe> {

    private static final JEIBlockRenderer blockRenderer = new JEIBlockRenderer();

    public ExtrusionCategory(Info<ExtrusionRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull ExtrusionRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 36)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.itemIngredient);
            
        builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 36)
            .setBackground(getRenderedSlot(), -1, -1)
			.addItemStack(recipe.getResultItem());
    };

    @Override
    public void draw(@Nonnull ExtrusionRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 26);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 39);
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(72, 27, 0);
        blockRenderer.renderBlock(CreateBlocks.EXTRUSION_DIE.getDefaultState().setValue(BlockStateProperties.AXIS, Axis.Z), graphics, 24);
        ms.popPose();;
    };
    
};
