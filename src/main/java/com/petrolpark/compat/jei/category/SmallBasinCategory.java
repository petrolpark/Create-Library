package com.petrolpark.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.mutable.MutableInt;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class SmallBasinCategory<R extends ProcessingRecipe<?, ?>> extends PetrolparkRecipeCategory<R> {

    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public SmallBasinCategory(CreateRecipeCategory.Info<R> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull RecipeHolder<R> holder, @Nonnull IFocusGroup focuses) {
        final R recipe = holder.value();
        addOptionalRequiredBiomeSlot(builder, recipe, 3, 3);
        addOptionalRecipeBookSlot(builder, holder, 22, 3);

        final List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

		int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
		int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;
		int i = 0;

		for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
			final List<ItemStack> stacks = new ArrayList<>();
			for (ItemStack itemStack : pair.getFirst().getItems()) {
				ItemStack copy = itemStack.copy();
				copy.setCount(pair.getSecond().getValue());
				stacks.add(copy);
			};

			builder.addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 29 - (i / 3) * 19)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStacks(stacks);
			i++;
		};

		for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
			int x = 17 + xOffset + (i % 3) * 19;
			int y = 29 - (i / 3) * 19;
			addFluidSlot(builder, x, y, fluidIngredient);
			i++;
		};

		size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
		i = 0;

		for (ProcessingOutput result : recipe.getRollableResults()) {
			int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
			int yPosition = -19 * (i / 2) + 29;

			builder.addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                .setBackground(getRenderedSlot(result), -1, -1)
                .addItemStack(result.getStack())
                .addRichTooltipCallback(addStochasticTooltip(result));
			i++;
		};

		for (FluidStack fluidResult : recipe.getFluidResults()) {
			int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
			int yPosition = -19 * (i / 2) + 29;
			addFluidSlot(builder, xPosition, yPosition, fluidResult);
			i++;
		};

		final HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (!requiredHeat.testBlazeBurner(HeatLevel.NONE)) {
			builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 59)
				.addItemStack(AllBlocks.BLAZE_BURNER.asStack());
		};
		if (!requiredHeat.testBlazeBurner(HeatLevel.KINDLED)) {
			builder.addSlot(RecipeIngredientRole.CATALYST, 153, 59)
				.addItemStack(AllItems.BLAZE_CAKE.asStack());
		};
    };

    @Override
    protected void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull R recipe, @Nonnull IFocusGroup focuses) {};

    @Override
    protected void draw(@Nonnull R recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();

		boolean noHeat = requiredHeat == HeatCondition.NONE;
		int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;
		if (vRows <= 2) AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 10);

		AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
		shadow.render(graphics, 81, 36 + (noHeat ? 10 : 30));

		AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
		heatBar.render(graphics, 4, 58);
		graphics.drawString(Minecraft.getInstance().font, CreateLang.translateDirect(requiredHeat.getTranslationKey()), 9, 64, requiredHeat.getColor(), false);
    
		if (requiredHeat != HeatCondition.NONE) heater.withHeat(requiredHeat.visualizeAsBlazeBurner()).draw(graphics, getBackground().getWidth() / 2 + 3, 33);
	};
    
};
