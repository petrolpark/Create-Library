package com.petrolpark.core.world.item.compression;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.fraction.BigFraction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class NoItemCompressionSequence implements IItemCompressionSequence {
    
    protected final ItemStack stack;
    protected final Optional<CompressedBlock> baseBlock;

    /**
     * @param stack Count is ignored
     */
    public NoItemCompressionSequence(ItemStack stack) {
        this.stack = stack.copyWithCount(1);
        baseBlock = stack.getItem() instanceof BlockItem blockItem ? Optional.of(new CompressedBlock(blockItem.getBlock(), this.stack)) : Optional.empty();
    };

    @Override
    public ItemStack getBaseItem() {
        return stack.copy();
    };

    @Override
    public List<ItemStack> getAllItems() {
        return Collections.singletonList(stack.copy());
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
    public BigFraction getEquivalentBaseItems(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, this.stack) ? BigFraction.getReducedFraction(stack.getCount(), 1) : null;
    };

    @Override
    public double getEquivalentBaseItems(ItemStack stack, double count) {
        return ItemStack.isSameItemSameComponents(stack, this.stack) ? count : 0d;
    };

    @Override
    public Optional<CompressedBlock> getBaseBlock() {
        return baseBlock;
    };
};
