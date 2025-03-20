package com.petrolpark.core.recipe.manualonly;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.PetrolparkTags.MenuTypes;
import com.petrolpark.core.recipe.ContainerCraftingInput;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public class ManualOnlyShapedRecipe extends ShapedRecipe {

    public static final MapCodec<ManualOnlyShapedRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(ManualOnlyShapedRecipe::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, ManualOnlyShapedRecipe> STREAM_CODEC = ShapedRecipe.Serializer.STREAM_CODEC.map(ManualOnlyShapedRecipe::new, Function.identity());

    public ManualOnlyShapedRecipe(ShapedRecipe recipe) {
        this(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.result, recipe.showNotification());
    };

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

    public static RecipeSerializer<ManualOnlyShapedRecipe> SERIALIZER = new RecipeSerializer<ManualOnlyShapedRecipe>() {

        @Override
        public MapCodec<ManualOnlyShapedRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ManualOnlyShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        };
        
    };
};
