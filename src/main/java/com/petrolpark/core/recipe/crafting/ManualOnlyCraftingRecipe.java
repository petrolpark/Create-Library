package com.petrolpark.core.recipe.crafting;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkTags.MenuTypes;
import com.petrolpark.core.recipe.ContainerCraftingInput;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ManualOnlyCraftingRecipe extends WrappedCraftingRecipe {

    public static boolean isAllowed(CraftingInput inv) {
        return inv instanceof ContainerCraftingInput containerInput && (containerInput.container.menu instanceof InventoryMenu || MenuTypes.ALLOWS_MANUAL_ONLY_CRAFTING.matches(containerInput.container.menu));
    };

    public ManualOnlyCraftingRecipe(CraftingRecipe wrappedRecipe) {
        super(wrappedRecipe);
    };

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        return super.matches(input, level) && isAllowed(input);
    };

    public ItemStack getExampleResult(final HolderLookup.Provider registries) {
        return getResultItem(registries);
    };

    @Override
    public RecipeSerializer<ManualOnlyCraftingRecipe> getSerializer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSerializer'");
    };
    
};
