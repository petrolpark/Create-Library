package com.petrolpark.core.wish;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.petrolpark.Petrolpark;
import com.petrolpark.core.recipe.ingredient.modifier.IForcingItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;

import net.createmod.catnip.data.IntAttached;
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

public abstract class WishList {

    public static final int DEFAULT_ATTEMPTS = 10;
    
    protected abstract List<IntAttached<IIngredientModifier<? super ItemStack>>> getWishes();

    public int getAttempts() {
        return DEFAULT_ATTEMPTS;
    };

    public final void addLootPoolWishedAndRandomItems(LootPool pool, Consumer<ItemStack> stackConsumer, LootContext context) {
        addLootPoolWishedAndRandomItems(pool, this, stackConsumer, context);
    };

    protected final boolean getLootTableWishedAndRandomItemsRaw(LootTable table, IIngredientModifier<? super ItemStack> wish, LootContext context, Consumer<ItemStack> output) {
        LootContext.VisitedEntry<?> visitedTableEntry = LootContext.createVisitedEntry(table);
        if (context.pushVisitedElement(visitedTableEntry)) {
            Consumer<ItemStack> consumer = LootItemFunction.decorate(table.compositeFunction, output, context);

            for (LootPool pool : table.pools) {
                addLootPoolWishedAndRandomItems(pool, wish, consumer, context);
                pool.addRandomItems(consumer, context);
            };

            context.popVisitedElement(visitedTableEntry);
        } else {
            Petrolpark.LOGGER.warn("Infinite loop in Loot Table while trying to grant wish");
        }
    };

    /**
     * 
     * @param pool
     * @param wishList
     * @param stackConsumer
     * @param context
     * @return Whether any wishes in the list came true
     */
    protected final boolean addLootPoolWishedAndRandomItems(LootPool pool, WishList wishList, Consumer<ItemStack> stackConsumer, LootContext context) {
        if (!pool.compositeCondition.test(context)) return false;

        int rolls = pool.getRolls().getInt(context) + Mth.floor(pool.getBonusRolls().getFloat(context) * context.getLuck());


    };

    /**
     * 
     * @param <MODIFIER>
     * @param pool
     * @param wish
     * @param stackConsumer
     * @param context
     * @return Whether the wish was succesfully fulfilled at least once
     */
    public final boolean addLootPoolWishedItem(LootPool pool, IIngredientModifier<? super ItemStack> wish, Consumer<ItemStack> stackConsumer, LootContext context) {
        for (LootPoolEntryContainer entryContainer : pool.entries) {

            if (entryContainer instanceof NestedLootTable lootTable) {

                return getLootTableWishedAndRandomItemsRaw(lootTable.contents, wish, context, stackConsumer);

            } else if (entryContainer instanceof LootPoolSingletonContainer singletonContainer) {

                int attempts = singletonContainer instanceof LootItem ? 1 : getAttempts();
                while (attempts > 0) {
                    attempts--;
                    boolean successful = false;
                    List<ItemStack> createdStacks = new ArrayList<>();
                    singletonContainer.createItemStack(createdStacks::add, context);
                    List<ItemStack> resultantItemStacks = new ArrayList<>(createdStacks.size());
                    for (ItemStack createdStack : createdStacks) { // Hopefully just one Item
                        Optional<ItemStack> resultantStack = Optional.of(createdStack);
                        if (wish instanceof IForcingItemIngredientModifier forcingWish) tryForceFunctions: for (LootItemFunction function : singletonContainer.functions) {
                            resultantStack = forcingWish.forceLootItemFunction(function, context, resultantStack.get());
                            if (resultantStack.isEmpty()) break tryForceFunctions;
                            //TODO roll function as normal if forcing fails
                        };
                        if (resultantStack.filter(wish::test).isPresent()) {
                            resultantItemStacks.add(resultantStack.get());
                            successful = true;
                        } else {
                            resultantItemStacks.add(createdStack);
                        };
                    };
                    if (successful) {
                        resultantItemStacks.forEach(stackConsumer);
                        return true;
                    };
                };
                return false;

            } else {

                List<LootPoolEntry> entries = Lists.newArrayList();
                entryContainer.expand(context, entry -> {
                    if (entry.getWeight(context.getLuck()) > 0) entries.add(entry);
                });
                for (LootPoolEntry entry : entries) {
                    int attempts = getAttempts();
                    while (attempts > 0) {
                        attempts--;
                        boolean successful = false;
                        List<ItemStack> createdStacks = new ArrayList<>();
                        entry.createItemStack(createdStacks::add, context);
                        tryEachItem: for (ItemStack createdStack : createdStacks) {
                            if (wish.test(createdStack)) {
                                successful = true;
                                break tryEachItem;
                            };
                        };
                        if (successful) {
                            createdStacks.forEach(stackConsumer);
                            return true;
                        };
                    };
                    return false;
                };

            };
        };
    };

    public class SingletonWishList extends WishList {

        public final IIngredientModifier<? super ItemStack> wish;
        protected final List<IntAttached<IIngredientModifier<? super ItemStack>>> list;

        public SingletonWishList(IIngredientModifier<? super ItemStack> wish) {
            this.wish = wish;
            list = List.of(IntAttached.with(1, wish));
        };

        @Override
        protected List<IntAttached<IIngredientModifier<? super ItemStack>>> getWishes() {
            return list;
        };

    };
};
