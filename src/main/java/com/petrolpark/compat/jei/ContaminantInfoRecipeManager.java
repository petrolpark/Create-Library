package com.petrolpark.compat.jei;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.compat.jei.category.ContaminantInfoCategory.ContaminantInfoRecipe;
import com.petrolpark.core.contamination.Contaminable;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.util.Pair;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;

@ParametersAreNonnullByDefault
public class ContaminantInfoRecipeManager<STACK> implements ISimpleRecipeManagerPlugin<ContaminantInfoRecipe<STACK>> {

    protected final Contaminable<?, STACK> contaminable;
    protected final IIngredientType<STACK> ingredientType;

    public ContaminantInfoRecipeManager(Contaminable<?, STACK> contaminable, IIngredientType<STACK> ingredientType) {
        this.contaminable = contaminable;
        this.ingredientType = ingredientType;
    };

    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        return contaminable.getContaminationOptional(input.cast(ingredientType).getIngredient()).filter(IContamination::hasAnyContaminant).isPresent();
    };

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        return isHandledInput(output);
    };

    @Override
    public List<ContaminantInfoRecipe<STACK>> getRecipesForInput(ITypedIngredient<?> input) {
        final STACK ingredient = input.cast(ingredientType).getIngredient();
        final IContamination<?, STACK> contamination = contaminable.getContamination(ingredient);
        if (contamination == null) return Collections.emptyList();
        final Optional<Pair<STACK, IContamination<?, STACK>>> pair = Optional.of(Pair.of(ingredient, contamination));
        return contamination.streamAllContaminants()
            .map(contaminant -> new ContaminantInfoRecipe<>(pair, contaminant))
            .toList();
    };

    @Override
    public List<ContaminantInfoRecipe<STACK>> getRecipesForOutput(ITypedIngredient<?> output) {
        return getRecipesForInput(output);
    };

    @Override
    public List<ContaminantInfoRecipe<STACK>> getAllRecipes() {
        return RegistryUtil.getRegistry(PetrolparkRegistries.Keys.CONTAMINANT).holders()
            .map(this::createUnboundRecipe)
            .toList();
    };

    protected ContaminantInfoRecipe<STACK> createUnboundRecipe(Holder<Contaminant> contaminant) {
        return new ContaminantInfoRecipe<>(Optional.empty(), contaminant);
    };
    
};
