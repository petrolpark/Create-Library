package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.badge.BadgeDuplicationRecipe;
import petrolpark.mc.library.core.data.recipe.ExampleRecipe;
import petrolpark.mc.library.core.flags.recipe.CombineFlaggedItemsRecipe;
import petrolpark.mc.library.core.world.item.crafting.BookRequiredCraftingRecipe;
import petrolpark.mc.library.core.world.item.crafting.WrappedCraftingRecipe;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.RecipeBookDuplicationRecipe;
import petrolpark.mc.library.core.world.item.recycling.DirectRecyclingRecipe;
import petrolpark.mc.library.core.world.item.recycling.IRecyclingRecipe;
import petrolpark.mc.library.core.world.item.recycling.IngredientRecyclingRecipe;
import petrolpark.mc.library.core.world.item.wooden.WoodCraftingShapedRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class PetrolparkRecipeSerializers {

    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<ExampleRecipe>> EXAMPLE = REGISTRATE.recipeSerializer("example", ExampleRecipe.CODEC, ExampleRecipe.STREAM_CODEC);
    public static final RegistryEntry<RecipeSerializer<?>, WoodCraftingShapedRecipe.Serializer> WOOD_CRAFTING_SHAPED = REGISTRATE.recipeSerializer("wood_crafting_shaped", WoodCraftingShapedRecipe.Serializer::new);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<BookRequiredCraftingRecipe>> CRAFTING_BOOK_REQUIRED = REGISTRATE.recipeSerializer("crafting_book_required", WrappedCraftingRecipe.serializer(BookRequiredCraftingRecipe::new));
    public static final RegistryEntry<RecipeSerializer<?>, IRecyclingRecipe.Serializer<DirectRecyclingRecipe>> RECYCLING = REGISTRATE.recipeSerializer("recycling", IRecyclingRecipe.serializer(DirectRecyclingRecipe::new));
    public static final RegistryEntry<RecipeSerializer<?>, IRecyclingRecipe.Serializer<IngredientRecyclingRecipe>> INGREDIENT_RECYCLING = REGISTRATE.recipeSerializer("ingredient_recycling", IRecyclingRecipe.serializer(IngredientRecyclingRecipe::new));
    //public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<ManualOnlyCraftingRecipe>> CRAFTING_MANUAL_ONLY = REGISTRATE.recipeSerializer("crafting_manual_only", WrappedCraftingRecipe.serializer(ManualOnlyCraftingRecipe::new));
    public static final RegistryEntry<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<CombineFlaggedItemsRecipe>> FLAGGED_ITEM_COMBINATION = REGISTRATE.recipeSerializer("flagged_item_combination", CombineFlaggedItemsRecipe::new);
    public static final RegistryEntry<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<BadgeDuplicationRecipe>> BADGE_DUPLICATION = REGISTRATE.recipeSerializer("badge_duplication", BadgeDuplicationRecipe::new);
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<RecipeBookDuplicationRecipe>> RECIPE_BOOK_DUPLICATION = REGISTRATE.recipeSerializer("recipe_book_duplication", RecipeBookDuplicationRecipe.CODEC, RecipeBookDuplicationRecipe.STREAM_CODEC);

    public static final void register() {};
};
