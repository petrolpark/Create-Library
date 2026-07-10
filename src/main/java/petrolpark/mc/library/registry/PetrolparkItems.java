package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.RecipeBookItem;
import petrolpark.mc.library.core.world.restaurant.RestaurantOrderBookItem;

public class PetrolparkItems {
    
    public static final ItemEntry<RestaurantOrderBookItem> ORDER_BOOK = REGISTRATE.item("order_book", RestaurantOrderBookItem::new)
        .defaultModel()
        .register();

    public static final ItemEntry<RecipeBookItem> RECIPE_BOOK = REGISTRATE.item("recipe_book", RecipeBookItem::new)
        .defaultModel()
        .tag(ItemTags.BOOKSHELF_BOOKS)
        .register();

    public static final void register() {};
};
