package com.petrolpark.core.item;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;

public class QueueItemHandler implements IItemHandler, INBTSerializable<ListTag> {

    protected final Queue<ItemStack> stacks = new LinkedList<>();

    public boolean isEmpty() {
        skimEmptyStacks();
        return stacks.isEmpty();
    };

    @Override
    public final int getSlots() {
        return 1;
    };

    public void skimEmptyStacks() {
        while (stacks.peek() != null && stacks.peek().isEmpty() && !stacks.isEmpty()) stacks.poll();
    };

    public ItemStack peekStack() {
        skimEmptyStacks();
        return Optional.ofNullable(stacks.peek()).orElse(ItemStack.EMPTY);
    };

    public ItemStack pollStack() {
        skimEmptyStacks();
        ItemStack stack = stacks.poll();
        if (stack == null) return ItemStack.EMPTY;
        if (!stack.isEmpty()) onContentsChanged();
        return stack;
    };

    public ItemStack add(ItemStack stack) {
        if (stack.isEmpty() || !isItemValid(stack)) return stack;
        ItemStack toInsert = stack.copy();
        for (ItemStack existing : stacks) {
            if (ItemStack.isSameItemSameComponents(existing, toInsert)) {
                int added = Math.min(toInsert.getCount(), Math.min(existing.getMaxStackSize(), getStackSizeLimit()) - existing.getCount());
                existing.grow(added);
                toInsert.shrink(added);
            };
        };
        while (!toInsert.isEmpty()) {
            int added = Math.min(toInsert.getCount(), getStackSizeLimit());
            stacks.add(toInsert.copyWithCount(added));
            toInsert.shrink(added);
        };
        onContentsChanged();
        return ItemStack.EMPTY;
    };

    @Override
    public final @NotNull ItemStack getStackInSlot(int slot) {
        return peekStack();
    };

    @Override
    public final @NotNull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (simulate) return isItemValid(stack) ? ItemStack.EMPTY : stack;
        return add(stack);
    };

    @Override
    public final @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = peekStack();
        if (amount >= stack.getCount()) return simulate ? stack : pollStack();
        ItemStack extracted = stack.copyWithCount(amount);
        if (!simulate) {
            stack.shrink(amount);
            onContentsChanged();
        };
        return extracted;
    };

    @Override
    public final int getSlotLimit(int slot) {
        return getStackSizeLimit();
    };

    public int getStackSizeLimit() {
        return Integer.MAX_VALUE;
    };

    @Override
    public final boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return isItemValid(stack);
    };

    public boolean isItemValid(ItemStack stack) {
        return true;
    };

    @Override
    public ListTag serializeNBT(@Nonnull HolderLookup.Provider registries) {
        ListTag listTag = new ListTag();
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            listTag.add(stack.save(registries));
        };
        return listTag;
    };

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider registries, @Nonnull ListTag nbt) {
        stacks.clear();
        for (int i = 0; i < nbt.size(); i++) {
            ItemStack stack = ItemStack.parseOptional(registries, nbt.getCompound(i));
            if (!stack.isEmpty()) stacks.add(stack);
        };
        onLoad();
    };

    protected void onContentsChanged() {};

    protected void onLoad() {};
};
