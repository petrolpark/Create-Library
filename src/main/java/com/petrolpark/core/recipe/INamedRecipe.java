package com.petrolpark.core.recipe;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.petrolpark.util.Lang;

import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface INamedRecipe {

    public static final String UNKNOWN_RECIPE_TRANSLATION_KEY = "recipe.petrolpark.unknown";

    public static Component unknownRecipeName() {
        return Component.translatable(UNKNOWN_RECIPE_TRANSLATION_KEY);
    };

    public static Component getName(RecipeHolder<?> recipeHolder) {
        return getName(recipeHolder.id(), recipeHolder.value());
    };

    @SuppressWarnings("null")
    public static Component getName(ResourceLocation id, Recipe<?> recipe) {
        if (recipe instanceof INamedRecipe namedRecipe) return namedRecipe.getName(id);

        try {
            ItemStack stack = recipe.getResultItem(null);
            if (!stack.isEmpty()) return stack.getHoverName();
        } catch(NullPointerException e) {};

        return unknownRecipeName();
    };
    
    public Component getName(ResourceLocation recipeId);

    public static Component cacheDefaultName(Component cachedName, Consumer<Component> cachedNameSetter, ResourceLocation recipeId, Supplier<List<Component>> outputNames) {
        if (cachedName == null) {
            String translationKey = Util.makeDescriptionId("recipe", recipeId);
            if (I18n.exists(translationKey)) {
                cachedName = Component.translatable(translationKey);
            } else {
                cachedName = Lang.shortList(outputNames.get(), 150);
            };
            cachedNameSetter.accept(cachedName);
        };
        return cachedName;
    };
};
