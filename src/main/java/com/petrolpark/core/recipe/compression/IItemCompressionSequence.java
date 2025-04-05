package com.petrolpark.core.recipe.compression;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.math.Fraction;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface IItemCompressionSequence {
    
    /**
     * Get a copy of the least-compressed Item (Stack) in the sequence.
     * @return ItemStack of count {@code 1}
     */
    public ItemStack getBaseItem();

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
