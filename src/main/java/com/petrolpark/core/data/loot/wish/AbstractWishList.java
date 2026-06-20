package com.petrolpark.core.data.loot.wish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.data.recipe.ingredient.advanced.IForcingItemAdvancedIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

@ApiStatus.Experimental
public abstract class AbstractWishList {

    public static final int DEFAULT_ATTEMPTS = 10;
    
    public abstract Collection<IAdvancedIngredient<? super ItemStack>> getWishes();

    /**
     * 
     * @param wish Should exist in {@link AbstractWishList#getWishes()}
     * @param maxFulfillments Maximum number of instances of that Wish to fulfill
     * @return Actual number of instances of that Wish that can be fulfilled (i.e. how many instances of that Wish are on the WishList)
     */
    public abstract int getWishInstanceCount(IAdvancedIngredient<? super ItemStack> wish, int maxFulfillments);

    /**
     * @param wish Should exist in {@link AbstractWishList#getWishes()}
     * @param stack
     */
    public abstract void fulfillWish(IAdvancedIngredient<? super ItemStack> wish, ItemStack stack);

    public int getAttempts() {
        return DEFAULT_ATTEMPTS;
    };

    public boolean addLootTableWishedAndRandomItemsRaw(LootTable table, Consumer<ItemStack> output, LootContext context) {
        return addLootTableWishedAndRandomItemsRaw(table, Collections.emptyList(), getWishes(), true, output, context);
    };

    /**
     * 
     * @param table
     * @param additionalFunctions {@link LootItemFunction}s to run after the built-in LootItemFunctions of this {@link LootTable}
     * @param wishes Subset of Wishes in this WishList to try and fulfill
     * @param fulfillWishes Whether to actually call {@link AbstractWishList#fulfillWish(IAdvancedIngredient, ItemStack)}
     * @param output
     * @param context
     * @return Whether any Wishes in the list came true
     */
    public boolean addLootTableWishedAndRandomItemsRaw(LootTable table, List<LootItemFunction> additionalFunctions, Collection<IAdvancedIngredient<? super ItemStack>> wishes, boolean fulfillWishes, Consumer<ItemStack> output, LootContext context) {
        final List<LootItemFunction> functions = Stream.concat(table.functions.stream(), additionalFunctions.stream()).toList();
        final LootContext.VisitedEntry<?> visitedTableEntry = LootContext.createVisitedEntry(table);
        if (context.pushVisitedElement(visitedTableEntry)) {

            boolean successful = false;
            for (LootPool pool : table.pools) {
                successful |= addLootPoolWishedAndRandomItems(pool, functions, wishes, fulfillWishes, output, context);
            };

            context.popVisitedElement(visitedTableEntry);
            
            return successful;
        } else {
            Petrolpark.LOGGER.warn("Infinite loop in Loot Table while trying to grant wish");
        }
        return false;
    };

    /**
     * 
     * @param pool
     * @param additionalFunctions {@link LootItemFunction}s to run after the built-in LootItemFunctions of this {@link LootPool}
     * @param wishes Subset of Wishes in this WishList to try and fulfill
     * @param fulfillWishes Whether to actually call {@link AbstractWishList#fulfillWish(IAdvancedIngredient, ItemStack)}
     * @param output
     * @param context
     * @return Whether any Wishes in the list came true
     */
    public boolean addLootPoolWishedAndRandomItems(LootPool pool, List<LootItemFunction> additionalFunctions, Collection<IAdvancedIngredient<? super ItemStack>> wishes, boolean fulfillWishes, Consumer<ItemStack> output, LootContext context) {
        if (!pool.compositeCondition.test(context)) return false;

        final List<ItemStack> allStacks = new ArrayList<>();
        boolean successful = false;
        int rolls = pool.getRolls().getInt(context) + Mth.floor(pool.getBonusRolls().getFloat(context) * context.getLuck());

        for (final IAdvancedIngredient<? super ItemStack> wish : wishes) {
            if (rolls <= 0) break;
            final List<ItemStack> addedStacks = new ArrayList<>();
            if (addLootPoolWishedItem(pool, additionalFunctions, wish, addedStacks::add, context)) {
                successful = true;
                final int fulfilledCount = getWishInstanceCount(wish, rolls);
                rolls -= fulfilledCount;
                for (int i = 0; i < fulfilledCount; i++) allStacks.addAll(addedStacks.stream().map(ItemStack::copy).toList());
            };
        };

        for (; rolls > 0; rolls--) pool.addRandomItem(stack -> {
            for (LootItemFunction function : additionalFunctions) stack = function.apply(stack, context);
            allStacks.add(stack);
        }, context); // If there are any remaining rolls, use them up with random rolls as usual

        for (final ItemStack stack : allStacks) {
            for (final IAdvancedIngredient<? super ItemStack> wish : wishes) {
                successful = true;
                if (fulfillWishes && wish.test(stack)) fulfillWish(wish, stack); // Additional wish.test here in case any Wishes were fulfilled randomly
            };
            output.accept(stack);
        };
        
        return successful;
    };

    /**
     * 
     * @param pool
     * @param wish
     * @param output
     * @param context
     * @return Whether the wish was succesfully fulfilled at least once
     */
    public boolean addLootPoolWishedItem(LootPool pool, List<LootItemFunction> additionalFunctions, IAdvancedIngredient<? super ItemStack> wish, Consumer<ItemStack> output, LootContext context) {
        final Stream<LootItemFunction> globalFunctions = Stream.concat(pool.functions.stream(), additionalFunctions.stream());
        for (LootPoolEntryContainer entryContainer : pool.entries) {

            if (entryContainer instanceof final IWishableLootPoolEntryContainer wishableContainer) {

                final List<ItemStack> resultantStacks =  wishableContainer.getWishedItems(this, wish, globalFunctions, context);
                if (!resultantStacks.isEmpty()) {
                    resultantStacks.forEach(output);
                    return true;
                };

            } else if (entryContainer instanceof final NestedLootTable lootTable) {

                if (addLootTableWishedAndRandomItemsRaw(
                    lootTable.contents.map(
                        rl -> context.getResolver().get(Registries.LOOT_TABLE, rl).map(Holder::value).orElse(LootTable.EMPTY),
                        Function.identity()
                    ), globalFunctions.toList(), Collections.singleton(wish), false, output, context
                )) return true;

            } else if (entryContainer instanceof final LootPoolSingletonContainer singletonContainer) {

                final List<LootItemFunction> functions = Stream.concat(singletonContainer.functions.stream(), globalFunctions).toList();

                int attempts = singletonContainer instanceof LootItem ? 1 : getAttempts();
                while (attempts > 0) {
                    attempts--;
                    boolean successful = false;
                    
                    final List<ItemStack> resultantStacks = new ArrayList<>();
                    singletonContainer.createItemStack(stack -> resultantStacks.add(forceFunctions(stack, wish, functions, context)), context);
                    tryEachItem: for (final ItemStack resultantStack : resultantStacks) { // Hopefully just one Item
                        if (wish.test(resultantStack)) {
                            successful = true;
                            break tryEachItem;
                        };
                    };
                    if (successful) {
                        resultantStacks.forEach(output);
                        return true;
                    };
                };

            } else {

                final List<LootItemFunction> functions = globalFunctions.toList();
                final List<LootPoolEntry> entries = new ArrayList<>();
                entryContainer.expand(context, entry -> {
                    if (entry.getWeight(context.getLuck()) > 0) entries.add(entry);
                });
                for (final LootPoolEntry entry : entries) {
                    int attempts = getAttempts();
                    while (attempts > 0) {
                        attempts--;
                        boolean successful = false;

                        final List<ItemStack> resultantStacks = new ArrayList<>();
                        entry.createItemStack(stack -> resultantStacks.add(forceFunctions(stack, wish, functions, context)), context);
                        tryEachItem: for (ItemStack resultantStack : resultantStacks) {
                            if (wish.test(resultantStack)) {
                                successful = true;
                                break tryEachItem;
                            };
                        };

                        if (successful) {
                            resultantStacks.forEach(output);
                            return true;
                        };
                    };

                };
            }
        };

        return false;
    };

    public ItemStack forceFunctions(ItemStack stack, IAdvancedIngredient<? super ItemStack> wish, List<LootItemFunction> functions, LootContext context) {
        if (wish instanceof IForcingItemAdvancedIngredient forcingWish) {
            for (LootItemFunction function : functions) {
                stack = forcingWish.forceLootItemFunction(function, context, stack).orElse(function.apply(stack, context));
            };
            return stack;
        } else {
            for (LootItemFunction function : functions) {
                stack = function.apply(stack, context);
            };
            return stack;
        }
    };
};
