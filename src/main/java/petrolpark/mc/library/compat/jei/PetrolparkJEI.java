package petrolpark.mc.library.compat.jei;

import java.util.Collections;

import javax.annotation.ParametersAreNonnullByDefault;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.jei.category.FlagInfoCategory;
import petrolpark.mc.library.compat.jei.category.IntrinsicFlagsCategory;
import petrolpark.mc.library.compat.jei.category.extension.WoodCraftingCategoryExtension;
import petrolpark.mc.library.compat.jei.category.recipeManagerPlugin.FlagInfoRecipeManager;
import petrolpark.mc.library.compat.jei.ingredient.BiomeIngredientType;
import petrolpark.mc.library.compat.jei.ingredient.BlockStateIngredientType;
import petrolpark.mc.library.compat.jei.ingredient.FlagIngredientType;
import petrolpark.mc.library.compat.jei.subtypeInterpreter.RecipeBookItemSubtypeInterpreter;
import petrolpark.mc.library.compat.jei.subtypeInterpreter.RestaurantMenuItemSubtypeInterpreter;
import petrolpark.mc.library.compat.jei.subtypeInterpreter.WoodenItemSubtypeInterpreter;
import petrolpark.mc.library.core.flags.Flaggables;
import petrolpark.mc.library.core.world.entity.player.extendedInventory.ExtendedInventoryJeiGuiHandler;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.RecipeBookItemJEICategoryCache;
import petrolpark.mc.library.core.world.item.wooden.WoodCraftingShapedRecipe;
import petrolpark.mc.library.registry.PetrolparkItems;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedBlocks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
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
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new FlagInfoCategory(guiHelper));
        registration.addRecipeCategories(new IntrinsicFlagsCategory<>(guiHelper, Registries.ITEM.location(), VanillaTypes.ITEM_STACK, IntrinsicFlagsCategory.ITEM_RECIPE_TYPE));
        registration.addRecipeCategories(new IntrinsicFlagsCategory<>(guiHelper, Registries.FLUID.location(), NeoForgeTypes.FLUID_STACK, IntrinsicFlagsCategory.FLUID_RECIPE_TYPE));

        BiomeIngredientType.HELPER.registerTagInfoCategory(registration);
        FlagIngredientType.HELPER.registerTagInfoCategory(registration);
    };

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        FlagInfoCategory.addRecipes(registration);
        registration.addRecipes(IntrinsicFlagsCategory.ITEM_RECIPE_TYPE, IntrinsicFlagsCategory.getAllRecipes(BuiltInRegistries.ITEM, Flaggables.ITEM));
        registration.addRecipes(IntrinsicFlagsCategory.FLUID_RECIPE_TYPE, IntrinsicFlagsCategory.getAllRecipes(BuiltInRegistries.FLUID, Flaggables.FLUID));

        BiomeIngredientType.HELPER.addTagRecipes(registration);
        FlagIngredientType.HELPER.addTagRecipes(registration);
    };

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(WoodCraftingShapedRecipe.class, new WoodCraftingCategoryExtension());
    };

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(BiomeIngredientType.TYPE, BiomeIngredientType.HELPER.streamAll().toList(), BiomeIngredientType.HELPER, BiomeIngredientType.RENDERER, BiomeIngredientType.HELPER.codec());
        registration.register(BlockStateIngredientType.TYPE, Collections.emptySet(), BlockStateIngredientType.HELPER, BlockStateIngredientType.RENDERER, BlockState.CODEC);
        registration.register(FlagIngredientType.TYPE, FlagIngredientType.HELPER.streamAll().toList(), FlagIngredientType.HELPER, FlagIngredientType.ICON_RENDERER, FlagIngredientType.HELPER.codec());
    };

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(PetrolparkItems.RECIPE_BOOK.get(), RecipeBookItemSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(PetrolparkItems.MENU.get(), RestaurantMenuItemSubtypeInterpreter.INSTANCE);
        if (SharedFeatureFlag.DRYING_RACK.enabled()) registration.registerSubtypeInterpreter(SharedBlocks.DRYING_RACK.asItem(), WoodenItemSubtypeInterpreter.INSTANCE);
    };

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addTypedRecipeManagerPlugin(FlagInfoCategory.RECIPE_TYPE, new FlagInfoRecipeManager<>(Flaggables.ITEM, VanillaTypes.ITEM_STACK));
        registration.addTypedRecipeManagerPlugin(FlagInfoCategory.RECIPE_TYPE, new FlagInfoRecipeManager<>(Flaggables.FLUID, NeoForgeTypes.FLUID_STACK));
    };

    @Override
    public ResourceLocation getPluginUid() {
        return Petrolpark.asResource("jei");
    };

    @Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new ExtendedInventoryJeiGuiHandler());
	};

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;
    };
    
};
