package com.petrolpark.core.badge;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public class BadgeDuplicationRecipe extends CustomRecipe {

    public static final RecipeSerializer<BadgeDuplicationRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(BadgeDuplicationRecipe::new);

    public BadgeDuplicationRecipe(CraftingBookCategory category) {
        super(category);
    };

    @Override
    public boolean matches(@Nonnull CraftingInput inv, @Nonnull Level level) {
        ItemStack badge = null;
        ItemStack duplicationStack = null;
        for(ItemStack stack : inv.items()) {
            if (stack.getItem() instanceof BadgeItem && badge == null) {
                badge = stack;
            } else if (duplicationStack == null) {
                duplicationStack = stack;
            } else {
                return false;
            };
        };
        if (badge == null) return false;
        Ingredient duplicationIngredient = ((BadgeItem)badge.getItem()).badge.get().getDuplicationIngredient();
        return duplicationIngredient != null && duplicationIngredient.test(duplicationStack);
    };

    @Override
    public ItemStack assemble(@Nonnull CraftingInput inv, @Nonnull HolderLookup.Provider registryAccess) {
        for (ItemStack stack : inv.items()) {
            if (stack.getItem() instanceof BadgeItem) return stack;
        };
        return ItemStack.EMPTY; // Shouldn't be called
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    };
    
};
