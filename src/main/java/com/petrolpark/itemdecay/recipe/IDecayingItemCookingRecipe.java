package com.petrolpark.itemdecay.recipe;

import com.petrolpark.Petrolpark;
import com.petrolpark.itemdecay.IDecayingItem;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

public interface IDecayingItemCookingRecipe<R extends AbstractCookingRecipe> extends Recipe<Container> {

    public RecipeSerializer<R> getWrappedSerializer();

    public R getAsWrappedRecipe();

    public static ItemStack withDecay(ItemStack stack) {
        if (!Petrolpark.DECAYING_ITEM_HANDLER.get().isClientSide()) {
            ItemStack copy = stack.copy();
            IDecayingItem.startDecay(copy, 0l);
            return copy;
        } else {
            return stack;
        }
    };

    public static class DecayingItemSmeltingRecipe extends SmeltingRecipe implements IDecayingItemCookingRecipe<SmeltingRecipe> {

        public DecayingItemSmeltingRecipe(AbstractCookingRecipe recipe) {
            super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getIngredients().get(0), recipe.getResultItem(null), recipe.getExperience(), recipe.getCookingTime());
        };

        @Override
        public ItemStack assemble(Container container, RegistryAccess registryAccess) {
            return withDecay(super.assemble(container, registryAccess));
        };

        @Override
        public ItemStack getResultItem(RegistryAccess registryAccess) {
            return withDecay(super.getResultItem(registryAccess));
        };

        @Override
        public RecipeSerializer<SmeltingRecipe> getWrappedSerializer() {
            return RecipeSerializer.SMELTING_RECIPE;
        };

        @Override
        public SmeltingRecipe getAsWrappedRecipe() {
            return this;
        };

    };

    public static class DecayingItemBlastingRecipe extends BlastingRecipe implements IDecayingItemCookingRecipe<BlastingRecipe> {

        public DecayingItemBlastingRecipe(AbstractCookingRecipe recipe) {
            super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getIngredients().get(0), recipe.getResultItem(null), recipe.getExperience(), recipe.getCookingTime());
        };

        @Override
        public ItemStack assemble(Container container, RegistryAccess registryAccess) {
            return withDecay(super.assemble(container, registryAccess));
        };

        @Override
        public ItemStack getResultItem(RegistryAccess registryAccess) {
            return withDecay(super.getResultItem(registryAccess));
        };

        @Override
        public RecipeSerializer<BlastingRecipe> getWrappedSerializer() {
            return RecipeSerializer.BLASTING_RECIPE;
        };

        @Override
        public BlastingRecipe getAsWrappedRecipe() {
            return this;
        };
    };

    public static class DecayingItemSmokingRecipe extends SmokingRecipe implements IDecayingItemCookingRecipe<SmokingRecipe> {

        public DecayingItemSmokingRecipe(AbstractCookingRecipe recipe) {
            super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getIngredients().get(0), recipe.getResultItem(null), recipe.getExperience(), recipe.getCookingTime());
        };

        @Override
        public ItemStack assemble(Container container, RegistryAccess registryAccess) {
            return withDecay(super.assemble(container, registryAccess));
        };

        @Override
        public ItemStack getResultItem(RegistryAccess registryAccess) {
            return withDecay(super.getResultItem(registryAccess));
        };

        @Override
        public RecipeSerializer<SmokingRecipe> getWrappedSerializer() {
            return RecipeSerializer.SMOKING_RECIPE;
        };

        @Override
        public SmokingRecipe getAsWrappedRecipe() {
            return this;
        };
    };
    
};
