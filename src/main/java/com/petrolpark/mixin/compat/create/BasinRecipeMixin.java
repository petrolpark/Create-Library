package com.petrolpark.mixin.compat.create;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.compat.create.core.recipe.firsttimelucky.IFTLProcessingRecipe;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.item.decay.ItemDecay;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;

@Mixin(BasinRecipe.class)
public class BasinRecipeMixin {
    
    /**
     * Start {@link ItemDecay} and propagate Contaminants in Basin Recipes.
     * @param basin
     * @param recipe
     * @param test
     * @param cir
     * @param isBasinRecipe
     * @param availableItems
     * @param availableFluids
     * @param heat
     * @param recipeOutputItems
     * @param recipeOutputFluids
     * @param ingredients
     * @param fluidIngredients
     * @param trueAndFalse
     * @param i1
     * @param i2
     * @param simulate
     * @param extractedItemsFromSlot
     * @param extractedFluidsFromTank
     */
    @Inject(
        method = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;acceptOutputs(Ljava/util/List;Ljava/util/List;Z)Z",
            shift = Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    private static void inApplyPropagateContaminants(
        BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir,
        boolean isBasinRecipe, IItemHandler availableItems, IFluidHandler availableFluids, BlazeBurnerBlock.HeatLevel heat,
        List<ItemStack> recipeOutputItems, List<FluidStack> recipeOutputFluids,
        List<Ingredient> ingredients, List<FluidIngredient> fluidIngredients,
        boolean trueAndFalse[], int i1, int i2, boolean simulate,
        int extractedItemsFromSlot[], int extractedFluidsFromTank[]
    ) {
        if (simulate) {
            recipeOutputItems.forEach(ItemDecay::startDecay);

            if (PetrolparkConfigs.server().createBasinRecipesPropagateContaminants.get()) {
                ItemStack[] itemInputs = new ItemStack[availableItems.getSlots()];
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    itemInputs[slot] = availableItems.getStackInSlot(slot).copyWithCount(extractedItemsFromSlot[slot]);
                };
                FluidStack[] fluidInputs = new FluidStack[availableFluids.getTanks()];
                for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                    FluidStack stack = availableFluids.getFluidInTank(tank).copy();
                    if (!stack.isEmpty()) stack.setAmount(extractedFluidsFromTank[tank]);
                    fluidInputs[tank] = stack;
                };

                Level level = basin.getLevel();
                if (level != null) IContamination.perpetuate(Stream.of(itemInputs), Stream.of(fluidInputs), PetrolparkConfigs.server().createFluidContaminantWeight.get(), recipeOutputItems.stream(), recipeOutputFluids.stream());
            };
        };
    };

    /**
     * Replace normal Recipe Outputs with First-Time-Lucky outputs, if applicable.
     * @param basinRecipe
     * @param original
     * @param basin
     * @param recipe
     * @param test
     */
    @WrapOperation(
        method = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
        at = @At(
            value = "INVOKE",
            target = "rollResults"
        ),
        remap = false
    )
    @SuppressWarnings("unchecked")
    private static final List<ItemStack> wrapRollResults(BasinRecipe basinRecipe, RandomSource random, Operation<List<ItemStack>> original, BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        if (basinRecipe instanceof IFTLProcessingRecipe ftlRecipe) return ftlRecipe.rollLuckyResults(basin, random);
        return original.call(basinRecipe, random);
    };
};
