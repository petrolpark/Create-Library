package com.petrolpark.core.data.recipe;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonElement;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public class WrappedRecipeManager extends RecipeManager {

    protected final RecipeManager wrapped;
    
    public WrappedRecipeManager(RecipeManager wrapped) {
        super(wrapped.registries);
        this.wrapped = wrapped;
    };

    @Override
    protected void apply(@Nonnull Map<ResourceLocation, JsonElement> object, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
        // NOOP
    };

    @Override
    public boolean hadErrorsLoading() {
        return wrapped.hadErrorsLoading();
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> recipeType, I input, Level level, @Nullable RecipeHolder<T> lastRecipe) {
        return wrapped.getRecipeFor(recipeType, input, level, lastRecipe);
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getAllRecipesFor(RecipeType<T> recipeType) {
        return wrapped.getAllRecipesFor(recipeType);
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getRecipesFor(RecipeType<T> recipeType, I input, Level level) {
        return wrapped.getRecipesFor(recipeType, input, level);
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> NonNullList<ItemStack> getRemainingItemsFor(RecipeType<T> recipeType, I input, Level level) {
        return wrapped.getRemainingItemsFor(recipeType, input, level);
    };

    @Override
    public Optional<RecipeHolder<?>> byKey(ResourceLocation recipeId) {
        return wrapped.byKey(recipeId);
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> type) {
        return wrapped.byType(type);
    };

    @Override
    public Collection<RecipeHolder<?>> getOrderedRecipes() {
        return wrapped.getOrderedRecipes();
    };

    @Override
    public Collection<RecipeHolder<?>> getRecipes() {
        return wrapped.getRecipes();
    };

    @Override
    public Stream<ResourceLocation> getRecipeIds() {
        return wrapped.getRecipeIds();
    };

    @Override
    public void replaceRecipes(Iterable<RecipeHolder<?>> recipes) {
        wrapped.replaceRecipes(recipes);
    };
};
