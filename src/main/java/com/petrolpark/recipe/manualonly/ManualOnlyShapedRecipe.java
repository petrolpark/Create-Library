package com.petrolpark.recipe.manualonly;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.PetrolparkTags.MenuTypes;
import com.petrolpark.recipe.ContainerCraftingInput;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public class ManualOnlyShapedRecipe extends ShapedRecipe {

    public ManualOnlyShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);
    };

    public static boolean isAllowed(CraftingInput inv) {
        return inv instanceof ContainerCraftingInput containerInput && (containerInput.container.menu instanceof InventoryMenu || MenuTypes.ALLOWS_MANUAL_ONLY_CRAFTING.matches(containerInput.container.menu));
    };

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        return super.matches(input, level) && isAllowed(input);
    };

    @Override
    public boolean isSpecial() {
        return true;
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PetrolparkRecipeTypes.MANUAL_ONLY_CRAFTING_SHAPED.getSerializer();
    };

    public ItemStack getExampleResult(final HolderLookup.Provider registries) {
        return getResultItem(registries);
    };

    public static class Serializer implements RecipeSerializer<ManualOnlyShapedRecipe> {

        private final ShapedRecipe.Serializer parent;

        public Serializer() {
            parent = new ShapedRecipe.Serializer();
        };

        @Override
        public ManualOnlyShapedRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            ShapedRecipe recipe = parent.fromJson(recipeId, serializedRecipe);
            return new ManualOnlyShapedRecipe(recipeId, recipe.getGroup(), recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null));
        };

        @Override
        public @Nullable ManualOnlyShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ShapedRecipe recipe = parent.fromNetwork(recipeId, buffer);
            return new ManualOnlyShapedRecipe(recipeId, recipe.getGroup(), recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null));
        };

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ManualOnlyShapedRecipe recipe) {
            parent.toNetwork(buffer, recipe);
        };


    };
    
};
