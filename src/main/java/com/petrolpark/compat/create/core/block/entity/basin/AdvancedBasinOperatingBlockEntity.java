package com.petrolpark.compat.create.core.block.entity.basin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.recipe.RecipeHelper;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.recipe.trie.AbstractVariant;
import com.simibubi.create.foundation.recipe.trie.RecipeTrie;
import com.simibubi.create.foundation.recipe.trie.RecipeTrieFinder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class AdvancedBasinOperatingBlockEntity extends BasinOperatingBlockEntity {

    public AdvancedBasinOperatingBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @SuppressWarnings("null")
    public static List<Recipe<?>> getMatchingRecipes(BasinBlockEntity basin, Object recipeCacheKey, Predicate<Recipe<?>> matchRecipeFilter, Predicate<RecipeHolder<?>> matchStaticFilters) {
        final List<Recipe<?>> list = new ArrayList<>();
        final Level level = basin.getLevel();
        if (level == null) return list;

        matchStaticFilters = matchStaticFilters.and(rh -> RecipeHelper.isValidAt(rh, level, basin.getBlockPos()));

		try {

			final IItemHandler availableItems = level.getCapability(ItemHandler.BLOCK, basin.getBlockPos(), null);
			final IFluidHandler availableFluids = level.getCapability(FluidHandler.BLOCK, basin.getBlockPos(), null);

			// no point even searching, since no recipe will ever match
			if (availableItems == null && availableFluids == null) return list;
			
			final RecipeTrie<?> trie = RecipeTrieFinder.get(recipeCacheKey, level, matchStaticFilters);
			Set<AbstractVariant> availableVariants = RecipeTrie.getVariants(availableItems, availableFluids);

			for (final Recipe<?> r : trie.lookup(availableVariants)) if (matchRecipeFilter.test(r)) list.add(r);

		} catch (Exception e) {
			Petrolpark.LOGGER.error("Failed to get recipe trie, falling back to slow logic", e);
			list.clear();

			for (final RecipeHolder<? extends Recipe<?>> r : RecipeFinder.get(recipeCacheKey, level, matchStaticFilters)) if (matchRecipeFilter.test(r.value())) list.add(r.value());
		};

		list.sort((r1, r2) -> r2.getIngredients().size() - r1.getIngredients().size());

		return list;
    };

    @Override
    protected List<Recipe<?>> getMatchingRecipes() {
		final Optional<BasinBlockEntity> basinOp = getBasin();
		final BasinBlockEntity basin;
		if (basinOp.isEmpty() || (basin = basinOp.get()).isEmpty()) return new ArrayList<>();

		return getMatchingRecipes(basin, getRecipeCacheKey(), this::matchBasinRecipe, this::matchStaticFilters);
	};

	protected Optional<ProcessingRecipe<?, ?>> getCurrentProcessingRecipe() {
        return currentRecipe instanceof ProcessingRecipe pr ? Optional.of(pr) : Optional.empty();
    };

    public abstract void updateRecipeCacheKey();
    
};