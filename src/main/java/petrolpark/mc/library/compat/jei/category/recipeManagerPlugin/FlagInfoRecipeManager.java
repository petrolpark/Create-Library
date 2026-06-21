package petrolpark.mc.library.compat.jei.category.recipeManagerPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import petrolpark.mc.library.compat.jei.category.FlagInfoCategory.FlagInfoRecipe;
import petrolpark.mc.library.compat.jei.ingredient.FlagIngredientType.FlagHolderHolder;
import petrolpark.mc.library.core.flags.Flaggable;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.Pair;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.common.util.RegistryUtil;

@ParametersAreNonnullByDefault
public class FlagInfoRecipeManager<STACK> implements ISimpleRecipeManagerPlugin<FlagInfoRecipe> {

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
    public List<FlagInfoRecipe> getRecipesForInput(ITypedIngredient<?> input) {
        final ITypedIngredient<STACK> typedIngredient = input.cast(ingredientType);
        if (typedIngredient == null) return Collections.emptyList();
        final STACK ingredient = typedIngredient.getIngredient();
        final IFlagPole<?, STACK> flags = flaggable.getFlagPole(ingredient);
        if (flags == null) return Collections.emptyList();
        final Optional<Pair<ITypedIngredient<?>, IFlagPole<?, ?>>> pair = Optional.of(Pair.of(typedIngredient, flags));
        return flags.streamAllFlags()
            .map(flag -> new FlagInfoRecipe(pair, new FlagHolderHolder(flag)))
            .toList();
    };

    @Override
    public List<FlagInfoRecipe> getRecipesForOutput(ITypedIngredient<?> output) {
        return getRecipesForInput(output);
    };

    @Override
    public List<FlagInfoRecipe> getAllRecipes() {
        return RegistryUtil.getRegistry(PetrolparkRegistries.Keys.FLAG).holders()
            .map(FlagInfoRecipe::new)
            .toList();
    };
    
};
