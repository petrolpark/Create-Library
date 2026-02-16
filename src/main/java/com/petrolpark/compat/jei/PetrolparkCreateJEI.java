package com.petrolpark.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkBlocks;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.CreateBlocks;
import com.petrolpark.compat.create.CreateRecipeTypes;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugationRecipe;
import com.petrolpark.compat.create.common.processing.centrifuge.PotionCentrifugation;
import com.petrolpark.compat.create.common.processing.centrifuge.PotionCentrifugation.PotionCentrifugationRecipe;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionRecipe;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerScreen;
import com.petrolpark.compat.jei.category.AgeingCategory;
import com.petrolpark.compat.jei.category.CentrifugationCategory;
import com.petrolpark.compat.jei.category.CropFertilizingCategory;
import com.petrolpark.compat.jei.category.DecayingItemCategory;
import com.petrolpark.compat.jei.category.DecayingItemCategory.DecayingItemRecipe;
import com.petrolpark.compat.jei.category.DryingCategory;
import com.petrolpark.compat.jei.category.ExtrusionCategory;
import com.petrolpark.compat.jei.category.LiddedBasinCategory;
import com.petrolpark.compat.jei.category.ManualOnlyCategory;
import com.petrolpark.compat.jei.category.builder.PetrolparkCategoryBuilder;
import com.petrolpark.compat.jei.ghost.PetrolparkGhostIngredientHandler;
import com.petrolpark.compat.jei.ingredient.BiomeIngredientType;
import com.petrolpark.compat.jei.ingredient.BlockStateIngredientType;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.item.decay.ageing.AgeingRecipe;
import com.petrolpark.core.item.decay.drying.DryingRecipe;
import com.petrolpark.core.recipe.CropFertilizingRecipe;
import com.petrolpark.core.recipe.crafting.ManualOnlyCraftingRecipe;
import com.petrolpark.mixin.compat.jei.client.ForgePluginFinderMixin;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * For now, this library's JEI plugin relies heavily on Create, so is set up to load only when Create is loaded.
 * This is done with {@link ForgePluginFinderMixin} instead of annotating this class.
 */
@RequiresCreate
public class PetrolparkCreateJEI implements IModPlugin {

    private static final List<CreateRecipeCategory<?>> ALL_CATEGORIES = new ArrayList<>(2);

    int itemDecayRecipeCount = 0;

    @SuppressWarnings({"null", "unused"})
    private void loadCategories(IJeiHelpers helpers) {
        ALL_CATEGORIES.clear();

        CreateRecipeCategory<?>

        ageing = builder(AgeingRecipe.class)
            .addTypedRecipes(PetrolparkRecipeTypes.AGEING::get)
            .catalyst(() -> Items.BARREL)
            .itemIcon(Items.BARREL)
            .emptyBackground(125, 20)
            .build("ageing", AgeingCategory::new),

        manual_crafting = builder(CraftingRecipe.class)
            .addTypedRecipesIf(() -> RecipeType.CRAFTING, rh -> rh.value() instanceof ManualOnlyCraftingRecipe)
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
            .addRecipes(helpers.getIngredientManager().getAllItemStacks().stream()
                .map(DecayingItemCategory::createRecipe)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(r -> new RecipeHolder<DecayingItemRecipe>(Petrolpark.asResource("decay_"+itemDecayRecipeCount++), r))
                ::toList
            ).itemIcon(Items.ROTTEN_FLESH)
            .emptyBackground(125, 20)
            .build("item_decay", DecayingItemCategory::new),

        crop_fertilizing = builder(CropFertilizingRecipe.class)
            .addTypedRecipes(PetrolparkRecipeTypes.CROP_FERTILIZING::get)
            .itemIcon(Items.BONE_MEAL)
            .emptyBackground(120, 125)
            .build("crop_fertilizing", CropFertilizingCategory::new);

        CreateRecipeCategory<?> centrifugation, potion_centrifugation, drying, extrusion, lidded_basin;

        if (SharedFeatureFlag.CENTRIFUGE.enabled()) {

            centrifugation = builder(CentrifugationRecipe.class)
                .addTypedRecipes(CreateRecipeTypes.CENTRIFUGATION)
                .catalyst(CreateBlocks.CENTRIFUGE::get)
                .itemIcon(CreateBlocks.CENTRIFUGE)
                .emptyBackground(120, 115)
                .build("centrifugation", CentrifugationCategory::new);

            if (PetrolparkConfigs.server().potionCentrifugation.get()) potion_centrifugation = builder(PotionCentrifugationRecipe.class)
                .addRecipes(PotionCentrifugation.streamAllRecipes(Minecraft.getInstance().getConnection().potionBrewing())::toList)
                .catalyst(CreateBlocks.CENTRIFUGE::get)
                .doubleItemIcon(CreateBlocks.CENTRIFUGE::asStack, () -> PotionContents.createItemStack(Items.POTION, Potions.HEALING))
                .emptyBackground(120, 115)
                .build("potion_centrifugation", CentrifugationCategory::new);
        };

        if (SharedFeatureFlag.DRYING_RACK.enabled()) drying = builder(DryingRecipe.class)
            .addTypedRecipes(PetrolparkRecipeTypes.DRYING::get)
            .catalyst(PetrolparkBlocks.DRYING_RACK::get)
            .itemIcon(PetrolparkBlocks.DRYING_RACK.get())
            .emptyBackground(125, 20)
            .build("drying", DryingCategory::new);

        if (SharedFeatureFlag.EXTRUSION.enabled()) extrusion = builder(ExtrusionRecipe.class)
            .addTypedRecipes(CreateRecipeTypes.EXTRUSION)
            .catalyst(CreateBlocks.EXTRUSION_DIE::get)
            .itemIcon(CreateBlocks.EXTRUSION_DIE.get())
            .emptyBackground(177, 55)
            .build("extrusion", ExtrusionCategory::new);

        if (SharedFeatureFlag.BASIN_LID.enabled()) lidded_basin = builder(BasinRecipe.class)
            .addTypedRecipes(CreateRecipeTypes.LIDDED_BASIN)
            .catalyst(CreateBlocks.BASIN_LID::get)
            .catalyst(AllBlocks.BASIN::get)
            .doubleItemIcon(CreateBlocks.BASIN_LID.get(), AllBlocks.BASIN.get())
            .emptyBackground(177, 103)
            .build("lidded_basin", LiddedBasinCategory::new);
    };

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        loadCategories(registration.getJeiHelpers());
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
        registration.register(BlockStateIngredientType.TYPE, Collections.emptySet(), BlockStateIngredientType.HELPER, BlockStateIngredientType.RENDERER, BlockState.CODEC);
    };

    private <T extends Recipe<?>> CategoryBuilderImpl<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilderImpl<>(recipeClass);
    };

    @Override
    public ResourceLocation getPluginUid() {
        return Petrolpark.asResource("create_jei");
    };

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(RedstoneProgrammerScreen.class, new PetrolparkGhostIngredientHandler());
    };

    private static class CategoryBuilderImpl<R extends Recipe<?>> extends PetrolparkCategoryBuilder<R, CategoryBuilderImpl<R>> {

        public CategoryBuilderImpl(Class<? extends R> recipeClass) {
            super(Petrolpark.MOD_ID, recipeClass, ALL_CATEGORIES::add);
        };

    };

};
