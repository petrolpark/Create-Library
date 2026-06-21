package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.data.recipe.ExampleRecipe;
import petrolpark.mc.library.core.world.item.crafting.BookRequiredCraftingRecipe;
import petrolpark.mc.library.core.world.item.recycling.DirectRecyclingRecipe;
import petrolpark.mc.library.core.world.item.recycling.IngredientRecyclingRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.RecipeType;

public class PetrolparkRecipeTypes {
    
    public static final RegistryEntry<RecipeType<?>, RecipeType<ExampleRecipe>> EXAMPLE = REGISTRATE.recipeType("example");
    public static final RegistryEntry<RecipeType<?>, RecipeType<DirectRecyclingRecipe>> RECYCLING = REGISTRATE.recipeType("recycling");
    public static final RegistryEntry<RecipeType<?>, RecipeType<IngredientRecyclingRecipe>> INGREDIENT_RECYCLING = REGISTRATE.recipeType("ingredient_recycling");
    public static final RegistryEntry<RecipeType<?>, RecipeType<BookRequiredCraftingRecipe>> CRAFTING_BOOK_REQUIRED = REGISTRATE.recipeType("book_required_crafting");

    public static final void register() {};
};
