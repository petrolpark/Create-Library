package com.petrolpark.compat.jei;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.extendedinventory.ExtendedInventoryJeiGuiHandler;
import com.petrolpark.core.recipe.book.RecipeBookItemJEICategoryCache;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

@JeiPlugin
public class PetrolparkJEI implements IModPlugin {

    public static final RecipeBookItemJEICategoryCache RECIPE_BOOK_ITEM_JEI_CATEGORY_CACHE = new RecipeBookItemJEICategoryCache();

    public static IJeiRuntime JEI_RUNTIME = null;

    public static final void ctor(IEventBus modEventBus, IEventBus mainEventBus) {
        mainEventBus.register(RECIPE_BOOK_ITEM_JEI_CATEGORY_CACHE);
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
