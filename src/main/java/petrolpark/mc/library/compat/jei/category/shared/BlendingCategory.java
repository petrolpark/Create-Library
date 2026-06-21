package petrolpark.mc.library.compat.jei.category.shared;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.compat.create.shared.registry.SharedPartialModels;
import petrolpark.mc.library.compat.jei.category.PetrolparkRecipeCategory;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BlendingCategory extends BasinCategory implements ISharedFeature {

    private final AnimatedBlender blender = new AnimatedBlender();

    public BlendingCategory(CreateRecipeCategory.Info<BasinRecipe> info, IJeiHelpers helpers) {
        super(info, false);
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
        blender.draw(graphics, getBackground().getWidth() / 2 + 3, 71);
    };

    public static class AnimatedBlender extends AnimatedKinetics {

        @Override
        public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
            final PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(xOffset, yOffset, 200);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
            final int scale = 23;

            blockElement(SharedPartialModels.BLENDER_COG)
                .rotateBlock(0, getCurrentAngle() * 2, 0)
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

            blockElement(SharedCreateBlocks.BLENDER.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

            blockElement(AllBlocks.BASIN.getDefaultState())
                .atLocal(0, -1, 0)
                .scale(scale)
                .render(graphics);

            matrixStack.popPose();
        }
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.BLENDER;
    };
    
};
