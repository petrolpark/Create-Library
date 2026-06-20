package com.petrolpark.compat.jei;

import javax.annotation.Nonnull;

import com.petrolpark.core.flags.IFlagPole;
import com.petrolpark.util.Lang;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;

public class FlagsTooltipCallback implements IRecipeSlotRichTooltipCallback {

    public static final FlagsTooltipCallback INSTANCE = new FlagsTooltipCallback();

    @Override
    public void onRichTooltip(@Nonnull IRecipeSlotView recipeSlotView, @Nonnull ITooltipBuilder tooltip) {
        IFlagPole.get(recipeSlotView.getDisplayedIngredient().map(ITypedIngredient::getIngredient))
            .ifPresent(flags -> Lang.addFlags(tooltip::add, flags, true));
    };
    
};
