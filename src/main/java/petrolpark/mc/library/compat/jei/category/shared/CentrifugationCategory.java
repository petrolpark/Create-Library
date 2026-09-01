package petrolpark.mc.library.compat.jei.category.shared;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.ICentrifugationRecipe;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.compat.create.shared.registry.SharedPartialModels;
import petrolpark.mc.library.compat.jei.category.PetrolparkRecipeCategory;
import petrolpark.mc.library.core.client.rendering.PetrolparkGuiTexture;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class CentrifugationCategory<R extends Recipe<?> & ICentrifugationRecipe> extends PetrolparkRecipeCategory<R> implements ISharedFeature {

    private static final AnimatedCentrifuge centrifuge = new AnimatedCentrifuge();

    public CentrifugationCategory(CreateRecipeCategory.Info<R> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull RecipeHolder<R> recipeHolder, @Nonnull IFocusGroup focuses) {
        final R recipe = recipeHolder.value();

        addOptionalRequiredBiomeSlot(builder, recipe, 80, 3);
        addOptionalRecipeBookSlot(builder, recipeHolder, 99, 3);

        final List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

		int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
		int i = 0;

		for (final SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
			addFluidSlot(builder, 3, 3 + i * 19, fluidIngredient);
			i++;
		};

        for (final Pair<Ingredient, MutableInt> pair : condensedIngredients) {
			final List<ItemStack> stacks = new ArrayList<>();
			for (ItemStack itemStack : pair.getFirst().getItems()) {
				ItemStack copy = itemStack.copy();
				copy.setCount(pair.getSecond().getValue());
				stacks.add(copy);
			};

			builder.addSlot(RecipeIngredientRole.INPUT, 3, 3 + i * 19)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStacks(stacks);
			i++;
		};

		size = recipe.getRollableResults().size();
        if (!recipe.getLightOutputFluid().isEmpty()) size++;
        final int xOffset = Math.max(3, 47 - 19 * size / 2);
		i = 0;

        if (!recipe.getLightOutputFluid().isEmpty()) addFluidSlot(builder, xOffset + (i++) * 19, 96, recipe.getLightOutputFluid());

		for (ProcessingOutput result : recipe.getRollableResults()) {

			builder.addSlot(RecipeIngredientRole.OUTPUT, xOffset + i * 19, 96)
                .setBackground(getRenderedSlot(result), -1, -1)
                .addItemStack(result.getStack())
                .addRichTooltipCallback(addStochasticTooltip(result));
			i++;
		};

        if (!recipe.getDenseOutputFluid().isEmpty()) addFluidSlot(builder, 99, 38, recipe.getDenseOutputFluid());
    };

    @Override
    protected void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull R recipe, @Nonnull IFocusGroup focuses) {
        //NOOP
    };

    @Override
    public void draw(@Nonnull R recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {

        AllGuiTextures.JEI_SHADOW.render(graphics, 20, 55);
        centrifuge.draw(graphics, 40, 60);

        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 34, 9);
        if (recipe.getFluidIngredients().size() >= 2 || !recipe.getRollableResults().isEmpty()) PetrolparkGuiTexture.JEI_SHORT_DOWN_ARROW.render(graphics, 38, 70);
        if (!recipe.getDenseOutputFluid().isEmpty()) PetrolparkGuiTexture.JEI_SHORT_RIGHT_ARROW.render(graphics, 72, 38);
    };

    public static class AnimatedCentrifuge extends AnimatedKinetics {

        @Override
        public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(xOffset, yOffset, 200);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(112.5f));
            int scale = 23;

            blockElement(SharedPartialModels.CENTRIFUGE_COG)
                .rotateBlock(0, getCurrentAngle() * 2, 0)
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

            blockElement(SharedCreateBlocks.CENTRIFUGE.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

            matrixStack.popPose();
        };
    };
    
    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.CENTRIFUGE;
    }
};
