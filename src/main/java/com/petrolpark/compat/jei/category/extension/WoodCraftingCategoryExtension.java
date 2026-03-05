package com.petrolpark.compat.jei.category.extension;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.petrolpark.core.item.wooden.WoodCraftingShapedRecipe;
import com.petrolpark.util.WoodHelper;
import com.petrolpark.util.WoodHelper.Wood;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.crafting.RecipeHolder;

public class WoodCraftingCategoryExtension implements ICraftingCategoryExtension<WoodCraftingShapedRecipe> {

    @Override
    public void setRecipe(@Nonnull RecipeHolder<WoodCraftingShapedRecipe> recipeHolder, @Nonnull IRecipeLayoutBuilder builder, @Nonnull ICraftingGridHelper craftingGridHelper, @Nonnull IFocusGroup focuses) {
        final List<Wood> woods = focuses.getItemStackFocuses().map(IFocus::getTypedValue).map(ITypedIngredient::getIngredient)
            .map(WoodCraftingShapedRecipe::getWood)
            .dropWhile(Objects::isNull)
            .distinct()
            .toList();

        if (woods.size() == 1) {
            setRecipeForWood(recipeHolder, woods.get(0), builder, craftingGridHelper);
        } else {
            craftingGridHelper.createAndSetIngredients(builder, recipeHolder.value().getIngredients(), getWidth(recipeHolder), getHeight(recipeHolder));
            craftingGridHelper.createAndSetOutputs(builder, WoodHelper.streamAllWoods().map(recipeHolder.value()::getResult).toList());
        };
    };

    public void setRecipeForWood(RecipeHolder<WoodCraftingShapedRecipe> recipeHolder, Wood wood, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper) {
        craftingGridHelper.createAndSetIngredients(builder, recipeHolder.value().streamSpecificIngredientsFor(wood).toList(), getWidth(recipeHolder), getHeight(recipeHolder));
        craftingGridHelper.createAndSetOutputs(builder, Collections.singletonList(recipeHolder.value().getResult(wood)));
    };
    
    // @Override
    // public void onDisplayedIngredientsUpdate(@Nonnull RecipeHolder<WoodCraftingShapedRecipe> recipeHolder, @Nonnull List<IRecipeSlotDrawable> recipeSlots, @Nonnull IFocusGroup focuses) {
    //     final WoodCraftingShapedRecipe recipe = recipeHolder.value();
    //     List<ItemStack> results = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT)
    //         .map(IFocus::getTypedValue)
    //         .map(ITypedIngredient::getIngredient)
    //         .toList();
    //     if (results.isEmpty()) {
    //         results = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
    //             .map(IFocus::getTypedValue)
    //             .map(ITypedIngredient::getIngredient)
    //             .map(WoodCraftingShapedRecipe::getWood)
    //             .map(recipe::getResult)
    //             .toList();
    //         if (results.isEmpty()) results = WoodHelper.streamAllWoods().map(recipe::getResult).toList();
    //     };
    //     final List<List<Ingredient>> ingredientStacks = results.stream().map(recipe::streamSpecificIngredientsFor).map(Stream::toList).toList();
    //     for (int i = 0; i < recipeSlots.size(); i++) {
    //         final IIngredientConsumer consumer = recipeSlots.get(i).createDisplayOverrides();
    //         ingredientStacks.get(i).forEach(consumer::addIngredients);
    //     };
    // };

    @Override
    public int getWidth(@Nonnull RecipeHolder<WoodCraftingShapedRecipe> recipeHolder) {
        return recipeHolder.value().getWidth();
    };

    @Override
    public int getHeight(@Nonnull RecipeHolder<WoodCraftingShapedRecipe> recipeHolder) {
        return recipeHolder.value().getHeight();
    };
};
