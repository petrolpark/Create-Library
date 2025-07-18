package com.petrolpark.compat.create.core.recipe.firsttimelucky;

import java.util.Optional;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.resources.ResourceLocation;

/**
 * Milling recipes which guarantee all chance outputs the first time they are done.
 */
@RequiresCreate
public class FTLMillingRecipe extends MillingRecipe implements IFTLProcessingRecipe<MillingRecipe> {

    public final Optional<ResourceLocation> firstTimeLuckyKey;

    public FTLMillingRecipe(ProcessingRecipeParams params) {
        super(params);
        firstTimeLuckyKey = params instanceof AdvancedProcessingRecipeParams advancedParams ? advancedParams.firstTimeLuckyKey() : Optional.empty();
    };

    @Override
    public Optional<ResourceLocation> getFirstTimeLuckyKey() {
        return firstTimeLuckyKey;
    };

    @Override
    public MillingRecipe getAsRecipe() {
        return this;
    };
};
