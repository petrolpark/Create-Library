package com.petrolpark.compat.jei;

import javax.annotation.Nonnull;

import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.util.Lang;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;

public class ContaminationTooltipCallback implements IRecipeSlotRichTooltipCallback {

    public static final ContaminationTooltipCallback INSTANCE = new ContaminationTooltipCallback();

    @Override
    public void onRichTooltip(@Nonnull IRecipeSlotView recipeSlotView, @Nonnull ITooltipBuilder tooltip) {
        IContamination.get(recipeSlotView.getDisplayedIngredient().map(ITypedIngredient::getIngredient))
            .ifPresent(contamination -> Lang.addContaminants(tooltip::add, contamination, true));
    };
    
};
