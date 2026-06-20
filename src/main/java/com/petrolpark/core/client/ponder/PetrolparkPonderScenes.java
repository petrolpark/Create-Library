package com.petrolpark.core.client.ponder;

import com.petrolpark.compat.Mods;
import com.petrolpark.core.world.item.crafting.recipeBook.RecipeBookScenes;
import com.petrolpark.registry.PetrolparkItems;
import com.petrolpark.shared.SharedFeatureFlag;
import com.petrolpark.shared.registry.SharedBlocks;
import com.petrolpark.shared.world.item.crafting.drying.rack.DryingRackScenes;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkPonderScenes {
    
    public static final void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        final PonderSceneRegistrationHelper<RegistryEntry<?, ?>> itemProviderHelper = helper.withKeyFunction(RegistryEntry::getId);

        if (SharedFeatureFlag.DRYING_RACK.enabled()) itemProviderHelper.addStoryBoard(SharedBlocks.DRYING_RACK, "processing/drying/rack", DryingRackScenes::dryingRack);

        itemProviderHelper.addStoryBoard(PetrolparkItems.RECIPE_BOOK, "recipe_book/vanilla", RecipeBookScenes::recipeBook);
        if (Mods.CREATE.isLoaded()) itemProviderHelper.addStoryBoard(PetrolparkItems.RECIPE_BOOK, "recipe_book/create", RecipeBookScenes::create);
    };
};
