package com.petrolpark.compat.jei.category.extension;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.core.item.wooden.WoodCraftingShapedRecipe;
import com.petrolpark.util.WoodHelper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

@ApiStatus.Experimental // not yet registered
public class WoodCraftingCategoryExtension implements ICraftingCategoryExtension<WoodCraftingShapedRecipe> {
    
    @Override
    public void onDisplayedIngredientsUpdate(@Nonnull RecipeHolder<WoodCraftingShapedRecipe> recipeHolder, @Nonnull List<IRecipeSlotDrawable> recipeSlots, @Nonnull IFocusGroup focuses) {
        final WoodCraftingShapedRecipe recipe = recipeHolder.value();
        List<ItemStack> results = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT)
            .map(IFocus::getTypedValue)
            .map(ITypedIngredient::getIngredient)
            .toList();
        if (results.isEmpty()) {
            results = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
                .map(IFocus::getTypedValue)
                .map(ITypedIngredient::getIngredient)
                .map(WoodCraftingShapedRecipe::getWood)
                .map(recipe::getResult)
                .toList();
            if (results.isEmpty()) results = WoodHelper.streamAllWoods().map(recipe::getResult).toList();
        };
        final List<List<Ingredient>> ingredientStacks = results.stream().map(recipe::streamSpecificIngredientsFor).map(Stream::toList).toList();
        for (int i = 0; i < recipeSlots.size(); i++) {
            final IIngredientConsumer consumer = recipeSlots.get(i).createDisplayOverrides();
            ingredientStacks.get(i).forEach(consumer::addIngredients);
        };
    };
};
