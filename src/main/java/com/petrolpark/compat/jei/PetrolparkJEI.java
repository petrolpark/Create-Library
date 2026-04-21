package com.petrolpark.compat.jei;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.compat.jei.category.ContaminantInfoCategory;
import com.petrolpark.compat.jei.category.ContaminantInfoCategory.ContaminantInfoRecipe;
import com.petrolpark.core.contamination.Contaminables;
import com.petrolpark.core.inventory.extended.ExtendedInventoryJeiGuiHandler;
import com.petrolpark.core.recipe.book.RecipeBookItemJEICategoryCache;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

@JeiPlugin
@ParametersAreNonnullByDefault
public class PetrolparkJEI implements IModPlugin {

    public static final RecipeBookItemJEICategoryCache RECIPE_BOOK_ITEM_JEI_CATEGORY_CACHE = new RecipeBookItemJEICategoryCache();

    public static IJeiRuntime JEI_RUNTIME = null;

    public static final void ctor(IEventBus modEventBus, IEventBus mainEventBus) {
        mainEventBus.register(RECIPE_BOOK_ITEM_JEI_CATEGORY_CACHE);
    };

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ContaminantInfoCategory<>(registration.getJeiHelpers().getGuiHelper(), VanillaTypes.ITEM_STACK, ContaminantInfoCategory.ITEM_RECIPE_TYPE));
    };

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(ContaminantInfoCategory.ITEM_RECIPE_TYPE, RegistryUtil.getRegistry(PetrolparkRegistries.Keys.CONTAMINANT).holders()
            .map(ContaminantInfoRecipe::forItemStacks)
            .toList()
        );
    };

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addTypedRecipeManagerPlugin(ContaminantInfoCategory.ITEM_RECIPE_TYPE, new ContaminantInfoRecipeManager<>(Contaminables.ITEM, VanillaTypes.ITEM_STACK));
    };

    @Override
    public ResourceLocation getPluginUid() {
        return Petrolpark.asResource("jei");
    };

    @Override
	public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new ExtendedInventoryJeiGuiHandler());
	};

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;
    };
    
};
