package com.petrolpark.compat.create.core.data.recipe;

import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.recipe.INamedRecipe;
import com.petrolpark.core.world.item.crafting.recipeBook.IBookRequiredRecipe;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.foundation.mixin.accessor.ShapedRecipeAccessor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public class RecipeBookMechanicalCraftingRecipe extends MechanicalCraftingRecipe implements IBookRequiredRecipe {

    public static final MapCodec<RecipeBookMechanicalCraftingRecipe> CODEC = MechanicalCraftingRecipe.Serializer.CODEC.<RecipeBookMechanicalCraftingRecipe>xmap(RecipeBookMechanicalCraftingRecipe::fromMechanicalCraftingRecipe, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeBookMechanicalCraftingRecipe> STREAM_CODEC = MechanicalCraftingRecipe.Serializer.STREAM_CODEC.<RecipeBookMechanicalCraftingRecipe>map(RecipeBookMechanicalCraftingRecipe::fromMechanicalCraftingRecipe, Function.identity());

    protected Component name = null;

    public RecipeBookMechanicalCraftingRecipe(String groupIn, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack recipeOutputIn, boolean acceptMirrored) {
        super(groupIn, category, pattern, recipeOutputIn, acceptMirrored);
    };

    @SuppressWarnings("null")
    private static RecipeBookMechanicalCraftingRecipe fromMechanicalCraftingRecipe(MechanicalCraftingRecipe recipe) {
		return new RecipeBookMechanicalCraftingRecipe(recipe.getGroup(), recipe.category(), ((ShapedRecipeAccessor) recipe).create$getPattern(), recipe.getResultItem(null), recipe.acceptsMirrored());
	};

    @Override
    public boolean isBookRequired(Level level) {
        return true;
    };

    protected void setName(Component name) {
        this.name = name;
    };

    @Override
    public Component getName(ResourceLocation recipeId) {
        return INamedRecipe.cacheDefaultName(name, this::setName, recipeId, () -> List.of(result.getHoverName()));
    };

    public static final class Serializer implements RecipeSerializer<RecipeBookMechanicalCraftingRecipe> {

        @Override
        public MapCodec<RecipeBookMechanicalCraftingRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RecipeBookMechanicalCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };
    
};
