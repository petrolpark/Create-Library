package petrolpark.mc.library.core.world.item.crafting;

import java.util.Collections;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.core.data.recipe.INamedRecipe;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.IBookRequiredRecipe;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.RecipeBookItem;
import petrolpark.mc.library.registry.PetrolparkRecipeSerializers;
import petrolpark.mc.library.registry.PetrolparkRecipeTypes;

public class BookRequiredCraftingRecipe extends WrappedCraftingRecipe implements IBookRequiredRecipe {

    public static Stream<RecipeHolder<BookRequiredCraftingRecipe>> streamMatching(Level level, CraftingInput input, @Nullable RecipeBook recipeBook, @Nullable Player player, @Nullable AbstractContainerMenu menu) {
        return level.getRecipeManager().getAllRecipesFor(PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get()).stream()
            .filter(rh -> 
                (recipeBook != null && recipeBook.contains(rh)) || // Player has used the Recipe Book to add the Recipe to their (Minecraft) Recipe Book
                (player != null && player.getInventory().hasAnyMatching(stack -> RecipeBookItem.streamProvidedRecipes(level, stack).anyMatch(rh::equals))) || // Player is carrying the Recipe Book
                (menu instanceof CraftingMenu craftingMenu && craftingMenu.access.evaluate((l, pos) -> IBookRequiredRecipe.hasRequiredBook(l, pos, rh), false)) // Crafting Table block is adjacent to Bookshelf supplying Recipe Book
            );
    };

    protected Component name = null;

    public BookRequiredCraftingRecipe(CraftingRecipe wrappedRecipe) {
        super(wrappedRecipe);
    };

    protected void setName(Component name) {
        this.name = name;
    };

    @Override
    @SuppressWarnings("null")
    public Component getName(ResourceLocation recipeId) {
        return INamedRecipe.cacheDefaultName(name, this::setName, recipeId, () -> Collections.singletonList(getResultItem(null).getHoverName()));
    };

    @Override
    public RecipeType<BookRequiredCraftingRecipe> getType() {
        return PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get();
    };

    @Override
    public RecipeSerializer<BookRequiredCraftingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.CRAFTING_BOOK_REQUIRED.get();
    };

    @Override
    public boolean isBookRequired(Level level) {
        return true;
    };

    
};
