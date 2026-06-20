package com.petrolpark.compat.jei.category.shared;

import java.util.List;
import java.util.stream.Stream;

import com.petrolpark.compat.jei.category.SimpleConversionCategory;
import com.petrolpark.core.world.item.decay.ItemDecay;
import com.petrolpark.shared.world.item.crafting.ageing.AgeingRecipe;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class AgeingCategory extends SimpleConversionCategory<AgeingRecipe> {

    public AgeingCategory(Info<AgeingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public Ingredient getInput(AgeingRecipe recipe, IFocusGroup focuses) {
        final List<IFocus<ItemStack>> inputs = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT).toList();
        return inputs.isEmpty() ? recipe.ingredient() : Ingredient.of(inputs.stream().map(IFocus::getTypedValue).map(ITypedIngredient::getIngredient).map(stack -> stack.copyWithCount(1)).map(ItemDecay::removeAppliedDecay));
    };

    @Override
    public List<ItemStack> getOutputs(AgeingRecipe recipe, IFocusGroup focuses) {
        return Stream.of(getInput(recipe, focuses).getItems()).map(recipe.decayProduct()::get).toList();
    };
    
};
