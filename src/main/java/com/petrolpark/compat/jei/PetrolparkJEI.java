package com.petrolpark.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.jei.category.DecayingItemCategory;
import com.petrolpark.compat.jei.category.DecayingItemCategory.DecayingItemRecipe;
import com.petrolpark.compat.jei.category.ManualOnlyCategory;
import com.petrolpark.compat.jei.category.builder.PetrolparkCategoryBuilder;
import com.petrolpark.recipe.manualonly.ManualOnlyShapedRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

@JeiPlugin
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
                    head.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), mc.player.getGameProfile()));
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
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        PetrolparkCategoryBuilder.helpers = registration.getJeiHelpers();
        registration.addRecipeCategories(ALL_CATEGORIES.toArray(IRecipeCategory[]::new));
    };

    @Override
	public void registerRecipes(IRecipeRegistration registration) {
        ALL_CATEGORIES.forEach(c -> c.registerRecipes(registration));
	};

    @Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		ALL_CATEGORIES.forEach(c -> c.registerCatalysts(registration));
	};

    private <T extends Recipe<?>> PetrolparkCategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new PetrolparkCategoryBuilder<>(Petrolpark.MOD_ID, recipeClass);
    };

    @Override
    public ResourceLocation getPluginUid() {
        return Petrolpark.asResource("jei");
    };


};
