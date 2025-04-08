package com.petrolpark.compat.jei.category;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.compat.jei.category.DecayingItemCategory.DecayingItemRecipe;
import com.petrolpark.core.item.decay.product.NoDecayProduct;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class DecayingItemCategory extends SimpleConversionCategory<DecayingItemRecipe> {

    public DecayingItemCategory(Info<DecayingItemRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public List<ItemStack> getInputs(DecayingItemRecipe recipe, IFocusGroup focuses) {
        return Collections.singletonList(recipe.decayingItem);
    };

    @Override
    public List<ItemStack> getOutputs(DecayingItemRecipe recipe, IFocusGroup focuses) {
        return Collections.singletonList(recipe.resultItem);
    };

    public static Optional<DecayingItemRecipe> createRecipe(ItemStack decayingItemStack) {
        if (decayingItemStack.has(PetrolparkDataComponents.DECAY_PRODUCT) && decayingItemStack.has(PetrolparkDataComponents.DECAY_TIME)) return Optional.of(new DecayingItemRecipe(decayingItemStack));
        return Optional.empty();
    };

    public static class DecayingItemRecipe extends ShapelessRecipe {

        public final ItemStack decayingItem;
        public final ItemStack resultItem;

        public DecayingItemRecipe(ItemStack decayingItem) {
            super("", CraftingBookCategory.MISC, ItemStack.EMPTY, NonNullList.create());
            this.decayingItem = decayingItem;
            this.resultItem = decayingItem.getOrDefault(PetrolparkDataComponents.DECAY_PRODUCT, NoDecayProduct.INSTANCE).get(decayingItem.copy());
        };

    };
};
