package petrolpark.mc.library.core.client.ponder;

import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.RecipeBookScenes;
import petrolpark.mc.library.registry.PetrolparkItems;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedBlocks;
import petrolpark.mc.library.shared.world.item.crafting.drying.rack.DryingRackScenes;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkPonderScenes {
    
    public static final void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        final PonderSceneRegistrationHelper<RegistryEntry<?, ?>> itemProviderHelper = helper.withKeyFunction(RegistryEntry::getId);

        if (SharedFeatureFlag.DRYING_RACK.enabled()) itemProviderHelper.addStoryBoard(SharedBlocks.DRYING_RACK, "shared/processing/drying/rack", DryingRackScenes::dryingRack);

        itemProviderHelper.addStoryBoard(PetrolparkItems.RECIPE_BOOK, "recipe_book/vanilla", RecipeBookScenes::recipeBook);
        if (Mods.CREATE.isLoaded()) itemProviderHelper.addStoryBoard(PetrolparkItems.RECIPE_BOOK, "recipe_book/create", RecipeBookScenes::create);
    };
};
