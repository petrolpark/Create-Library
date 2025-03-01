package com.petrolpark.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.ArrayList;

public class BigItemStack {

    public static final BigItemStack EMPTY = new BigItemStack(ItemStack.EMPTY);
    
    protected final ItemStack stack;
    protected final int count;

    public BigItemStack(ItemStack stack) {
        this(stack, stack.getCount());
    };

    public BigItemStack(ItemStack stack, int count) {
        this.stack = stack.copyWithCount(1);
        this.count = count;
    };

    public BigItemStack(ItemLike item, int count) {
        this.stack = new ItemStack(item);
        this.count = count;
    };

    public ItemStack getSingleItemStack() {
        return stack;
    };

    public int getCount() {
        return count;
    };

    public List<ItemStack> getAsStacks() {
        int leftOver = count % stack.getMaxStackSize();
        List<ItemStack> stacks = new ArrayList<>(count / stack.getMaxStackSize() + leftOver == 0 ? 0 : 1);
        for (int i = 0; i < count / stack.getMaxStackSize(); i++) stacks.add(stack.copyWithCount(stack.getMaxStackSize()));
        if (leftOver > 0) stacks.add(stack.copyWithCount(leftOver));
        return stacks;
    };

    public ItemStack copyStackWithCount(int count) {
        return stack.copyWithCount(count);
    };

    public BigItemStack copyWithCount(int count) {
        return new BigItemStack(stack, count);
    };
};
