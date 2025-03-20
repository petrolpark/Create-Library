package com.petrolpark.compat.create.core.recipe.firsttimelucky;

import com.petrolpark.RequiresCreate;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

/**
 * Milling recipes which guarantee all chance outputs the first time they are done.
 */
@RequiresCreate
public class FTLMillingRecipe extends MillingRecipe implements IFTLProcessingRecipe<MillingRecipe> {

    public FTLMillingRecipe(ProcessingRecipeParams params) {
        super(params);
    };

    @Override
    public MillingRecipe getAsRecipe() {
        return this;
    };

    @Override
    public boolean shouldBeLuckyFirstTime() {
        return true;
    }

    @Override
    public void setLuckyFirstTime(boolean lucky) {
        // Do nothing, this type of recipe is always lucky the first time
    };
};
