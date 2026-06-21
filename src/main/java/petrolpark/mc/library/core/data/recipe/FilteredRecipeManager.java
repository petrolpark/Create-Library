package petrolpark.mc.library.core.data.recipe;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import petrolpark.mc.library.util.RandomHelper;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public class FilteredRecipeManager extends WrappedRecipeManager {

    protected final Multimap<RecipeType<?>, RecipeHolder<?>> byType = MultimapBuilder.hashKeys().arrayListValues().build();
    
    public Predicate<? super RecipeHolder<?>> filter = $ -> true;

    public FilteredRecipeManager(RecipeManager wrapped) {
        super(wrapped);
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> recipeType, I input, Level level, @Nullable RecipeHolder<T> lastRecipe) {
        if (input.isEmpty()) return Optional.empty();
        return lastRecipe != null && lastRecipe.value().matches(input, level)
            ? Optional.of(lastRecipe)
            : Optional.ofNullable(RandomHelper.pick(level.getRandom(), byType(recipeType).stream().filter(filter).filter(rh -> rh.value().matches(input, level)).toList()));
        
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getAllRecipesFor(RecipeType<T> recipeType) {
        return super.getAllRecipesFor(recipeType).stream().filter(filter).toList();
    };

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getRecipesFor(RecipeType<T> recipeType, I input, Level level) {
        return super.getRecipesFor(recipeType, input, level).stream().filter(filter).toList();
    };

    @Override
    public Collection<RecipeHolder<?>> getOrderedRecipes() {
        return super.getOrderedRecipes().stream().filter(filter).toList();
    };

    @Override
    public Collection<RecipeHolder<?>> getRecipes() {
        return super.getRecipes().stream().filter(filter).collect(Collectors.toSet());
    };
    
};
