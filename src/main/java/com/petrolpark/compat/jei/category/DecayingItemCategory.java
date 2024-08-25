package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.destroy.compat.jei.category.DecayingItemCategory.DecayingItemRecipe;
import com.petrolpark.destroy.item.IDecayingItem;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class DecayingItemCategory extends DestroyRecipeCategory<DecayingItemRecipe> {

    public DecayingItemCategory(Info<DecayingItemRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DecayingItemRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(recipe.decayingItem)
            .addTooltipCallback(addFluidTooltip(1));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(recipe.resultItem)
            .addTooltipCallback(addFluidTooltip(1));
    };

    @Override
    public void draw(DecayingItemRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 27, 6);
    };

    public static class DecayingItemRecipe extends ShapelessRecipe {

        public final ItemStack decayingItem;
        public final ItemStack resultItem;

        public DecayingItemRecipe(ItemStack decayingItem) {
            super(ForgeRegistries.ITEMS.getKey(decayingItem.getItem()).withPath(path -> path + "_decay"), "", CraftingBookCategory.MISC, ItemStack.EMPTY, NonNullList.create());
            this.decayingItem = decayingItem;
            this.resultItem = ((IDecayingItem)decayingItem.getItem()).getDecayProduct(decayingItem);
        };

    };
};
