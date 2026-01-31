package com.petrolpark.core.recipe.compression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.util.BigItemStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface IItemCompressionSequence {
    
    /**
     * Get a copy of the least-compressed Item (Stack) in the sequence.
     * @return ItemStack of count {@code 1}
     */
    public ItemStack getBaseItem();

    /**
     * Get the current List of Item Stacks in this sequence. Not guaranteed to be the entire sequence.
     * @return Non-{@code null} list of ItemStacks of count {@code 1}.
     * @see IItemCompressionSequence#getAllItems()
     */
    @ApiStatus.Internal
    default List<ItemStack> getKnownItems() {
        return getAllItems();
    };

    /**
     * Get the ordered list of all Item (Stacks) in this sequence.
     * @return Non-{@code null} list of ItemStacks of count {@code 1}
     */
    public List<ItemStack> getAllItems();

    /**
     * The number of {@link IItemCompressionSequence#getAllItems() Items} in this sequence.
     * @return Positive number
     */
    public default int size() {
        return getAllItems().size();
    };

    /**
     * An ordered list of {@link IItemCompression}s, with the {@link IItemCompression#count() counts} relative to the Item before
     * (i.e. not relative to the {@link IItemCompressionSequence#getBaseItem() base Item}, except for the first compression).
     * @return Non-{@code null} list of {@link IItemCompression}s
     */
    public List<IItemCompression> getAllCompressions();

    /**
     * Get the number of {@link IItemCompressionSequence#getBaseItem() base Items} equivalent to the given Item Stack.
     * @param stack The count of the Stack is considered
     * @return {@code null} if the Item (considering its Components) are not part of this sequence
     * @see IItemCompressionSequence#getEquivalentBaseItems(int)
     */
    public Fraction getEquivalentBaseItems(ItemStack stack);

    /**
     * Get the number of {@link IItemCompressionSequence#getBaseItem() base Items} a given amount of the given Item.
     * @param stack The count of this Stack is ignored
     * @param count
     * @return {@code 0} if the Item (considering its Components) are not part of this sequence
     */
    public double getEquivalentBaseItems(ItemStack stack, double count);

    /**
     * Get the number of {@link IItemCompressionSequence#getBaseItem() base Items} equivalent to the {@code item}th {@link IItemCompressionSequence#getAllItems() Item (Stack) in this sequence}.
     * @param item
     * @return {@code null} if {@code item} is outside the bounds of the number of Items in this sequence.
     * @see IItemCompressionSequence#getEquivalentBaseItems(ItemStack)
     */
    public default Fraction getEquivalentBaseItems(int item) {
        if (item < 0 || item >= size()) return null;
        return getEquivalentBaseItems(getAllItems().get(item));
    };

    /**
     * Get the least-compressed Item which is a BlockItem.
     * @return Empty Optional if there are no BlockItems in this sequence, or an Optional containing a {@link IItemCompressionSequence.CompressedBlock} containing the Block and the ItemStack pertaining to it
     * (which is guaranteed to be {@link IItemCompressionSequence#getEquivalentBaseItems(ItemStack) in} this sequence)
     */
    public Optional<CompressedBlock> getBaseBlock();

    public record CompressedBlock(Block block, ItemStack stack) {};

    public default boolean isEmpty() {
        return false;
    };

    /**
     * Divide a number of {@link IItemCompressionSequence#getBaseItem() base Items} into the biggest possible compressed forms, producing the smallest total number of Item Stacks.
     * @param baseItemCount
     */
    public default List<BigItemStack> getFewestStacks(long baseItemCount) {
        if (baseItemCount <= 0) return Collections.emptyList();
        List<BigItemStack> stacks = new ArrayList<>(size());
        for (int item = size() - 1; item >= 0; item--) {
            long amount = Fraction.getFraction((int)baseItemCount, 1).divideBy(getEquivalentBaseItems(item)).longValue();
            if (amount == 0) continue;
            stacks.add(new BigItemStack(getAllItems().get(item), amount));
            baseItemCount -= Fraction.getFraction((int)amount, 1).multiplyBy(getEquivalentBaseItems(item)).longValue();
        };
        return stacks;
    };

    public static final IItemCompressionSequence EMPTY = new EmptyItemCompressionSequence();

    static class EmptyItemCompressionSequence implements IItemCompressionSequence {

        protected EmptyItemCompressionSequence() {};

        @Override
        public ItemStack getBaseItem() {
            return ItemStack.EMPTY;
        };

        @Override
        public List<ItemStack> getAllItems() {
            return Collections.singletonList(ItemStack.EMPTY);
        };

        @Override
        public int size() {
            return 1;
        };

        @Override
        public List<IItemCompression> getAllCompressions() {
            return Collections.emptyList();
        };

        @Override
        public Fraction getEquivalentBaseItems(ItemStack stack) {
            return null;
        };

        @Override
        public double getEquivalentBaseItems(ItemStack stack, double count) {
            return 0d;
        };

        @Override
        public Fraction getEquivalentBaseItems(int item) {
            return item == 0 ? Fraction.ZERO : null;
        };

        @Override
        public Optional<CompressedBlock> getBaseBlock() {
            return Optional.empty();
        };

        @Override
        public boolean isEmpty() {
            return true;
        };
    };
};
