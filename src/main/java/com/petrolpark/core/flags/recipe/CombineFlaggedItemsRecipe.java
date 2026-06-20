package com.petrolpark.core.flags.recipe;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkDataComponentTypes;
import com.petrolpark.PetrolparkRecipeSerializers;
import com.petrolpark.core.flags.IFlagPole;
import com.petrolpark.core.flags.ItemFlagPole;
import com.petrolpark.util.ItemHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CombineFlaggedItemsRecipe extends CustomRecipe implements IHandleFlagsMyselfRecipe<CraftingInput> {

    public CombineFlaggedItemsRecipe(CraftingBookCategory category) {
        super(category);
    };

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        ItemStack firstStack = ItemStack.EMPTY;
        boolean atLeastTwo = false;
        boolean atLeastOneFlag = false;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) continue;
            if (ItemFlagPole.get(stack).hasAnyExtrinsicFlag()) atLeastOneFlag = true;
            if (firstStack.isEmpty()) {
                firstStack = stack;
            } else {
                if (!ItemHelper.equalIgnoringComponents(stack, firstStack, PetrolparkDataComponentTypes.ORPHAN_FLAGS)) return false;
                atLeastTwo = true;
            };
        };
        return atLeastTwo && atLeastOneFlag;
    };

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider registries) {
        ItemStack result = ItemStack.EMPTY;
        int count = 0;
        for (ItemStack stack : input.items()) {
            if (!stack.isEmpty()) {
                count++;
                if (result.isEmpty()) result = stack;
            };
        };
        result = result.copyWithCount(count);
        IFlagPole<?, ?> flags = ItemFlagPole.get(result);
        flags.clearFlags();
        ItemFlagPole.perpetuateSingle(input.items().stream(), result);
        return result;
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    };

    @Override
    public RecipeSerializer<CombineFlaggedItemsRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.FLAGGED_ITEM_COMBINATION.get();
    };

    @Override
    public boolean isFlagsHandled(CraftingInput input, HolderLookup.Provider registries) {
        return true;
    };
    
};
