package com.petrolpark.recipe.manualonly;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.petrolpark.PetrolparkTags.PetrolparkMenuTypeTags;
import com.petrolpark.recipe.PetrolparkRecipeTypes;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class ManualOnlyShapedRecipe extends ShapedRecipe {

    public ManualOnlyShapedRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result) {
        super(id, group, category, width, height, recipeItems, result);
    };

    public static boolean isAllowed(CraftingContainer inv) {
        return inv instanceof TransientCraftingContainer container && (container.menu instanceof InventoryMenu || PetrolparkMenuTypeTags.ALLOWS_MANUAL_ONLY_CRAFTING.matches(container.menu));
    };

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        return super.matches(inv, level) && isAllowed(inv);
    };

    @Override
    public boolean isSpecial() {
        return true;
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PetrolparkRecipeTypes.MANUAL_ONLY_CRAFTING_SHAPED.getSerializer();
    };

    public ItemStack getExampleResult() {
        return getResultItem(null);
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
