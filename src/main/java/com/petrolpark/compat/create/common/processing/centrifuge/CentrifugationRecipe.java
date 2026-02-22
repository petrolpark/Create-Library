package com.petrolpark.compat.create.common.processing.centrifuge;

import java.util.List;

import javax.annotation.Nonnull;

import com.petrolpark.compat.create.CreateRecipeTypes;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipe;
import com.petrolpark.compat.create.core.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CentrifugationRecipe extends AdvancedProcessingRecipe<RecipeInput> implements ICentrifugationRecipe {

    public CentrifugationRecipe(AdvancedProcessingRecipeParams params) {
        super(CreateRecipeTypes.CENTRIFUGATION, params);
    };

    @Override
    public boolean matches(@Nonnull RecipeInput input, @Nonnull Level level) {
        return false;
    };

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    };

    @Override
    protected int getMaxInputCount() {
        return 64;
    };

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    };

    @Override
    protected int getMaxOutputCount() {
        return 4;
    };

    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    };

    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull RecipeInput input) {
        return super.getRemainingItems(input);
    };

    @Override
    public NonNullList<Ingredient> getCentrifugationIngredients() {
        return getIngredients();
    };

    @Override
    public List<ItemStack> rollLuckyResults(SmartBlockEntity blockEntity, RandomSource random) {
        return super.rollLuckyResults(blockEntity, random);
    };

    @Override
    public FluidStack getDenseOutputFluid() {
        return getFluidResults().size() >= 1 ? getFluidResults().get(0) : FluidStack.EMPTY;
    };

    @Override
    public FluidStack getLightOutputFluid() {
        return getFluidResults().size() >= 2 ? getFluidResults().get(1) : FluidStack.EMPTY;
    };
    
};
