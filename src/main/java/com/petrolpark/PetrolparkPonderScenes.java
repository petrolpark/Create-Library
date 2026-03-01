package com.petrolpark;

import com.petrolpark.compat.Mods;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorScenes;
import com.petrolpark.core.item.decay.drying.rack.DryingRackScenes;
import com.petrolpark.core.recipe.book.RecipeBookScenes;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkPonderScenes {
    
    public static final void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        final PonderSceneRegistrationHelper<RegistryEntry<?, ?>> itemProviderHelper = helper.withKeyFunction(RegistryEntry::getId);

        if (SharedFeatureFlag.DRYING_RACK.enabled()) itemProviderHelper.addStoryBoard(PetrolparkBlocks.DRYING_RACK, "processing/drying/rack", DryingRackScenes::dryingRack);
        if (Mods.CREATE.isLoaded()) itemProviderHelper.addStoryBoard(PetrolparkBlocks.DRYING_RACK, "processing/drying/chain_conveyor", ChainConveyorScenes::drying);

        itemProviderHelper.addStoryBoard(PetrolparkItems.RECIPE_BOOK, "recipe_book/vanilla", RecipeBookScenes::recipeBook);
        if (Mods.CREATE.isLoaded()) itemProviderHelper.addStoryBoard(PetrolparkItems.RECIPE_BOOK, "recipe_book/create", RecipeBookScenes::create);
    };
};
