package com.petrolpark.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BigItemStack {

    public static final BigItemStack EMPTY = new BigItemStack(ItemStack.EMPTY);
    
    protected final ItemStack stack;
    protected final long count;

    public BigItemStack(ItemStack stack) {
        this(stack, stack.getCount());
    };

    public BigItemStack(ItemStack stack, long count) {
        this.stack = stack.copyWithCount(1);
        this.count = count;
    };

    public BigItemStack(ItemLike item, long count) {
        this.stack = new ItemStack(item);
        this.count = count;
    };

    public ItemStack getSingleItemStack() {
        return stack.copy();
    };

    public long getCount() {
        return count;
    };

    public List<ItemStack> getAsStacks() {
        long leftOver = count % stack.getMaxStackSize();
        List<ItemStack> stacks = new ArrayList<>(count / stack.getMaxStackSize() + leftOver == 0 ? 0 : 1);
        for (long i = 0; i < count / stack.getMaxStackSize(); i++) stacks.add(stack.copyWithCount(stack.getMaxStackSize()));
        if (leftOver > 0) stacks.add(stack.copyWithCount((int)leftOver));
        return stacks;
    };

    public ItemStack copyStackWithCount(int count) {
        return stack.copyWithCount(count);
    };

    public BigItemStack copyWithCount(long count) {
        return new BigItemStack(stack, count);
    };
};
