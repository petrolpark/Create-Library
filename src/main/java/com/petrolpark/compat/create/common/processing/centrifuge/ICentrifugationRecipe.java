package com.petrolpark.compat.create.common.processing.centrifuge;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkCriteriaTriggers;
import com.petrolpark.compat.create.core.block.entity.behaviour.AdvancementBehaviour;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;

public interface ICentrifugationRecipe {

	public NonNullList<Ingredient> getIngredients();

	public NonNullList<SizedFluidIngredient> getFluidIngredients();

    public int getProcessingDuration();

	/**
	 * @see ICentrifugationRecipe#rollLuckyResults(SmartBlockEntity, RandomSource)
	 */
    public List<ProcessingOutput> getRollableResults();
    
    public List<ItemStack> rollLuckyResults(SmartBlockEntity blockEntity, RandomSource random);

    public NonNullList<ItemStack> getRemainingItems(@Nonnull RecipeInput input);

    public FluidStack getDenseOutputFluid();
    
    public FluidStack getLightOutputFluid();

    /**
     * Copied from {@link BasinRecipe#apply}
     */
    @SuppressWarnings("null")
    public default boolean apply(CentrifugeBlockEntity centrifuge, boolean test) {
        final IItemHandler availableItems = centrifuge.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, centrifuge.getBlockPos(), Direction.UP);
		final IFluidHandler availableFluids = centrifuge.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, centrifuge.getBlockPos(), Direction.UP);
        if (availableItems == null || availableFluids == null) return false;

        boolean filterMatched = centrifuge.filter.test(getDenseOutputFluid()) || centrifuge.filter.test(getLightOutputFluid());
        if (!filterMatched) for (final ProcessingOutput result : getRollableResults()) {
            if (centrifuge.filter.test(result.getStack())) {
                filterMatched = true;
                break;
            };
        };
        if (!filterMatched) return false;

        final List<ItemStack> recipeOutputItems = new ArrayList<>();

        for (boolean simulate : Iterate.trueAndFalse) {
            if (!simulate && test) return true;

            final int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
			final int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

            Ingredients:
			for (final Ingredient ingredient : getIngredients()) {
				for (int slot = 0; slot < availableItems.getSlots(); slot++) {
					if (simulate && availableItems.getStackInSlot(slot).getCount() <= extractedItemsFromSlot[slot]) continue;
					final ItemStack extracted = availableItems.extractItem(slot, 1, true);
					if (!ingredient.test(extracted)) continue;
					if (!simulate) availableItems.extractItem(slot, 1, false);
					extractedItemsFromSlot[slot]++;
					continue Ingredients;
				};

				// Something wasn't found
				return false;
			};

			FluidIngredients:
			for (final SizedFluidIngredient fluidIngredient : getFluidIngredients()) {
				int amountRequired = fluidIngredient.amount();

				for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
					final FluidStack fluidStack = availableFluids.getFluidInTank(tank);
					if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank]) continue;
					if (!fluidIngredient.test(fluidStack)) continue;
					final int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
					if (!simulate) fluidStack.shrink(drainedAmount);
					amountRequired -= drainedAmount;
					if (amountRequired != 0) continue;
					extractedFluidsFromTank[tank] += drainedAmount;
					continue FluidIngredients;
				};

				// Something wasn't found
				return false;
			};

			if (simulate) {
				final CraftingInput remainderInput = new DummyCraftingContainer(availableItems, extractedItemsFromSlot).asCraftInput();
                recipeOutputItems.addAll(rollLuckyResults(centrifuge, centrifuge.getLevel().random));
                for (ItemStack stack : getRemainingItems(remainderInput)) if (!stack.isEmpty()) recipeOutputItems.add(stack);
			};

			if (!centrifuge.acceptOutputs(recipeOutputItems, getDenseOutputFluid(), getLightOutputFluid(), simulate)) return false;
        };

        if (!test) BlockEntityBehaviour.get(centrifuge, AdvancementBehaviour.TYPE).award(PetrolparkCriteriaTriggers.CENTRIFUGE.get().trigger(recipeOutputItems, getDenseOutputFluid(), getLightOutputFluid()));

        return true;
    };
};
