package com.petrolpark.recipe.contamination;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.contamination.IContamination;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.util.ItemHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CombineContaminatedItemsRecipe extends CustomRecipe implements IHandleContaminationMyselfRecipe<CraftingInput> {

    public static final MapCodec<CombineContaminatedItemsRecipe> CODEC = MapCodec.unit(CombineContaminatedItemsRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, CombineContaminatedItemsRecipe> STREAM_CODEC = StreamCodec.unit(new CombineContaminatedItemsRecipe());

    public CombineContaminatedItemsRecipe() {
        super(CraftingBookCategory.MISC);
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
                if (!ItemHelper.equalIgnoringComponents(stack, firstStack, PetrolparkDataComponents.ORPHAN_CONTAMINANTS)) return false;
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
        contamination.fullyDecontaminate(registries);
        ItemContamination.perpetuateSingle(registries, input.items().stream(), result);
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
    public boolean isContaminationHandled(CraftingInput input, HolderLookup.Provider registries) {
        return true;
    };

    public static final RecipeSerializer<CombineContaminatedItemsRecipe> SERIALIZER = new RecipeSerializer<CombineContaminatedItemsRecipe>() {

        @Override
        public MapCodec<CombineContaminatedItemsRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CombineContaminatedItemsRecipe> streamCodec() {
            return STREAM_CODEC;
        };
        
    };

    
};
