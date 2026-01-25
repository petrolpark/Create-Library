package com.petrolpark.core.contamination.recipe;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkDataComponentTypes;
import com.petrolpark.PetrolparkRecipeSerializers;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.util.ItemHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CombineContaminatedItemsRecipe extends CustomRecipe implements IHandleContaminationMyselfRecipe<CraftingInput> {

    public CombineContaminatedItemsRecipe(CraftingBookCategory category) {
        super(category);
    };

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        ItemStack firstStack = ItemStack.EMPTY;
        boolean atLeastTwo = false;
        boolean atLeastOneContaminant = false;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) continue;
            if (ItemContamination.get(stack).hasAnyExtrinsicContaminant()) atLeastOneContaminant = true;
            if (firstStack.isEmpty()) {
                firstStack = stack;
            } else {
                if (!ItemHelper.equalIgnoringComponents(stack, firstStack, PetrolparkDataComponentTypes.ORPHAN_CONTAMINANTS)) return false;
                atLeastTwo = true;
            };
        };
        return atLeastTwo && atLeastOneContaminant;
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
        IContamination<?, ?> contamination = ItemContamination.get(result);
        contamination.fullyDecontaminate();
        ItemContamination.perpetuateSingle(input.items().stream(), result);
        return result;
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    };

    @Override
    public RecipeSerializer<CombineContaminatedItemsRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.CONTAMINATED_ITEM_COMBINATION.get();
    };

    @Override
    public boolean isContaminationHandled(CraftingInput input, HolderLookup.Provider registries) {
        return true;
    };
    
};
