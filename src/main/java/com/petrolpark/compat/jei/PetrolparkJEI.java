package com.petrolpark.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.jei.category.DecayingItemCategory;
import com.petrolpark.compat.jei.category.DecayingItemCategory.DecayingItemRecipe;
import com.petrolpark.compat.jei.category.ManualOnlyCategory;
import com.petrolpark.compat.jei.category.builder.PetrolparkCategoryBuilder;
import com.petrolpark.compat.jei.ingredient.BiomeIngredientType;
import com.petrolpark.mixin.compat.jei.client.JustEnoughItemsClientMixin;
import com.petrolpark.recipe.manualonly.ManualOnlyShapedRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

/**
 * For now, this library's JEI plugin relies heavily on Create, so is set up to load only when Create is loaded.
 * This is done with {@link JustEnoughItemsClientMixin} instead of annotating this class.
 */
public class PetrolparkJEI implements IModPlugin {

    private static final List<CreateRecipeCategory<?>> ALL_CATEGORIES = new ArrayList<>(2);

    @SuppressWarnings("unused")
    private void loadCategories() {
        ALL_CATEGORIES.clear();

        CreateRecipeCategory<?>

        manual_crafting = builder(CraftingRecipe.class)
            .addTypedRecipesIf(() -> RecipeType.CRAFTING, r -> r instanceof ManualOnlyShapedRecipe)
            .catalyst(() -> Blocks.CRAFTING_TABLE)
            .doubleItemIcon(
                () -> new ItemStack(Items.CRAFTING_TABLE),
                () -> {
                    Minecraft mc = Minecraft.getInstance();
                    ItemStack head = new ItemStack(Items.PLAYER_HEAD);
                    if (mc.player != null) head.set(DataComponents.PROFILE, new ResolvableProfile(mc.player.getGameProfile()));
                    return head;
                }
            )
            .emptyBackground(116, 56)
            .build("manual_crafting", ManualOnlyCategory::new),

        item_decay = builder(DecayingItemRecipe.class)
            .addRecipes(() -> JEISetup.DECAYING_ITEMS.stream().map(Supplier::get).map(DecayingItemRecipe::new).toList())
            .itemIcon(Items.ROTTEN_FLESH)
            .emptyBackground(125, 20)
            .build("item_decay", DecayingItemCategory::new);
    };

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        loadCategories();
        PetrolparkCategoryBuilder.helpers = registration.getJeiHelpers();
        registration.addRecipeCategories(ALL_CATEGORIES.toArray(IRecipeCategory[]::new));
    };

    @Override
	public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        ALL_CATEGORIES.forEach(c -> c.registerRecipes(registration));
	};

    @Override
	public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
		ALL_CATEGORIES.forEach(c -> c.registerCatalysts(registration));
	};

    @Override
    public void registerIngredients(@Nonnull IModIngredientRegistration registration) {
        registration.register(BiomeIngredientType.TYPE, Collections.emptySet(), BiomeIngredientType.HELPER, BiomeIngredientType.RENDERER, BiomeIngredientType.HELPER.getRegistry().byNameCodec());
    };

    private <T extends Recipe<?>> CategoryBuilderImpl<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilderImpl<>(recipeClass);
    };

    @Override
    public ResourceLocation getPluginUid() {
        return Petrolpark.asResource("jei");
    };

    private static class CategoryBuilderImpl<R extends Recipe<?>> extends PetrolparkCategoryBuilder<R, CategoryBuilderImpl<R>> {

        public CategoryBuilderImpl(Class<? extends R> recipeClass) {
            super(Petrolpark.MOD_ID, recipeClass, ALL_CATEGORIES::add);
        };

    };

};
