package com.petrolpark.recipe.contamination;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.petrolpark.contamination.IContamination;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.util.ItemHelper;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CombineContaminatedItemsRecipe extends CustomRecipe implements IHandleContaminationMyself<CraftingContainer> {

    public static final Serializer SERIALIZER = new Serializer();

    public CombineContaminatedItemsRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    };

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack firstStack = ItemStack.EMPTY;
        boolean atLeastTwo = false;
        boolean atLeastOneContaminant = false;
        for (ItemStack stack : container.getItems()) {
            if (stack.isEmpty()) continue;
            if (ItemContamination.get(stack).hasAnyExtrinsicContaminant()) atLeastOneContaminant = true;
            if (firstStack.isEmpty()) {
                firstStack = stack;
            } else {
                if (!ItemHelper.equalIgnoringComponents(stack, firstStack, ItemContamination.TAG_KEY)) return false;
                atLeastTwo = true;
            };
        };
        return atLeastTwo && atLeastOneContaminant;
    };

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = ItemStack.EMPTY;
        int count = 0;
        for (ItemStack stack : container.getItems()) {
            if (!stack.isEmpty()) {
                count++;
                if (result.isEmpty()) result = stack;
            };
        };
        result = result.copyWithCount(count);
        IContamination<?, ?> contamination = ItemContamination.get(result);
        contamination.fullyDecontaminate();
        ItemContamination.perpetuateSingle(container.getItems().stream(), result);
        return result;
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    };

    @Override
    public boolean contaminationHandled(CraftingContainer container, RegistryAccess registryAccess) {
        return true;
    };

    public static class Serializer implements RecipeSerializer<CombineContaminatedItemsRecipe> {

        @Override
        public CombineContaminatedItemsRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            return new CombineContaminatedItemsRecipe(recipeId);
        };

        @Override
        public @Nullable CombineContaminatedItemsRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new CombineContaminatedItemsRecipe(recipeId);
        };

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CombineContaminatedItemsRecipe pRecipe) {};

    };

    
};
