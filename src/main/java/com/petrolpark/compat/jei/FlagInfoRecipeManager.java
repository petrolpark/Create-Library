package com.petrolpark.compat.jei;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.compat.jei.category.FlagInfoCategory.FlagInfoRecipe;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.flags.Flaggable;
import com.petrolpark.core.flags.IFlagPole;
import com.petrolpark.registry.PetrolparkRegistries;
import com.petrolpark.util.Pair;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;

@ParametersAreNonnullByDefault
public class FlagInfoRecipeManager<STACK> implements ISimpleRecipeManagerPlugin<FlagInfoRecipe<STACK>> {

    protected final Flaggable<?, STACK> flaggable;
    protected final IIngredientType<STACK> ingredientType;

    public FlagInfoRecipeManager(Flaggable<?, STACK> flaggable, IIngredientType<STACK> ingredientType) {
        this.flaggable = flaggable;
        this.ingredientType = ingredientType;
    };

    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        return Optional.ofNullable(input.cast(ingredientType))
            .map(ITypedIngredient::getIngredient)
            .flatMap(flaggable::getFlagPoleOptional)
            .filter(IFlagPole::hasAnyFlag)
            .isPresent();
    };

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        return isHandledInput(output);
    };

    @Override
    public List<FlagInfoRecipe<STACK>> getRecipesForInput(ITypedIngredient<?> input) {
        final ITypedIngredient<STACK> typedIngredient = input.cast(ingredientType);
        if (typedIngredient == null) return Collections.emptyList();
        final STACK ingredient = typedIngredient.getIngredient();
        final IFlagPole<?, STACK> flags = flaggable.getFlagPole(ingredient);
        if (flags == null) return Collections.emptyList();
        final Optional<Pair<STACK, IFlagPole<?, STACK>>> pair = Optional.of(Pair.of(ingredient, flags));
        return flags.streamAllFlags()
            .map(flag -> new FlagInfoRecipe<>(pair, flag))
            .toList();
    };

    @Override
    public List<FlagInfoRecipe<STACK>> getRecipesForOutput(ITypedIngredient<?> output) {
        return getRecipesForInput(output);
    };

    @Override
    public List<FlagInfoRecipe<STACK>> getAllRecipes() {
        return RegistryUtil.getRegistry(PetrolparkRegistries.Keys.FLAG).holders()
            .map(this::createUnboundRecipe)
            .toList();
    };

    protected FlagInfoRecipe<STACK> createUnboundRecipe(Holder<Flag> flag) {
        return new FlagInfoRecipe<>(Optional.empty(), flag);
    };
    
};
