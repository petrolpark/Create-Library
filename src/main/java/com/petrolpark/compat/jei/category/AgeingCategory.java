package com.petrolpark.compat.jei.category;

import java.util.List;
import java.util.stream.Stream;

import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.core.item.decay.ageing.AgeingRecipe;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

public class AgeingCategory extends SimpleConversionCategory<AgeingRecipe> {

    public AgeingCategory(Info<AgeingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public List<ItemStack> getInputs(AgeingRecipe recipe, IFocusGroup focuses) {
        return streamInputs(recipe, focuses).map(recipe::setDecayProductAndTime).toList();
    };

    @Override
    public List<ItemStack> getOutputs(AgeingRecipe recipe, IFocusGroup focuses) {
        return streamInputs(recipe, focuses).map(recipe.product()::get).toList();
    };

    protected Stream<ItemStack> streamInputs(AgeingRecipe recipe, IFocusGroup focuses) {
        List<IFocus<ItemStack>> inputs = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT).toList();
        if (inputs.isEmpty()) return Stream.of(recipe.ingredient().getItems()).map(ItemStack::copy);
        return inputs.stream().map(IFocus::getTypedValue).map(ITypedIngredient::getIngredient).map(stack -> stack.copyWithCount(1)).map(ItemDecay::removeAppliedDecay);
    };
    
};
