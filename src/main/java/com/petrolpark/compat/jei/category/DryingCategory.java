package com.petrolpark.compat.jei.category;

import java.util.List;

import javax.annotation.Nonnull;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.item.decay.drying.DryingRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

public class DryingCategory extends SimpleConversionCategory<DryingRecipe> implements ISharedFeature {

    public DryingCategory(CreateRecipeCategory.Info<DryingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public Ingredient getInput(DryingRecipe recipe, IFocusGroup focuses) {
        return recipe.ingredient();
    };

    @Override
    public List<ItemStack> getOutputs(DryingRecipe recipe, IFocusGroup focuses) {
        return recipe.streamResults().toList();
    };

    @Override
    public void registerCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        if (PetrolparkConfigs.server().createChainConveyorDrying.get()) registration.addRecipeCatalysts(type, AllBlocks.CHAIN_CONVEYOR, Blocks.CHAIN);
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.DRYING_RACK;
    };

};
