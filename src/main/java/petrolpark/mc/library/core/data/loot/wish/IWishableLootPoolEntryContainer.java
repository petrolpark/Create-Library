package petrolpark.mc.library.core.data.loot.wish;

import java.util.List;
import java.util.stream.Stream;

import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public interface IWishableLootPoolEntryContainer {
    
    /**
     * @param wishList
     * @param wish
     * @param lootItemFunctions Additional Loot Item Functions that <strong>must be applied</strong> to any returned ItemStacks
     * @param context
     * @return Collection of ItemStacks matching the {@code wish}, or an empty List if no matching Stacks can be generated
     */
    public List<ItemStack> getWishedItems(AbstractWishList wishList, IAdvancedIngredient<? super ItemStack> wish, Stream<LootItemFunction> lootItemFunctions, LootContext context);
};
