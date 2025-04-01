package com.petrolpark.core.recipe.compression;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.math.Fraction;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;

public class CompressionItemHandler implements IItemHandler, INBTSerializable<CompoundTag> {

    /**
     * Capacity of {@link IItemCompressionSequence#getBaseItem() base Items}.
     */
    protected int capacity;

    /**
     * Number of {@link IItemCompressionSequence#getBaseItem() base Items}.
     */
    protected int count = 0;

    protected IItemCompressionSequence sequence;

    public CompressionItemHandler(IItemCompressionSequence sequence, int capacity) {
        this.sequence = sequence;
        this.capacity = capacity;
    };

    @Override
    public int getSlots() {
        return sequence.size();
    };

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return sequence.getAllItems().get(slot).copyWithCount(sequence.getEquivalentBaseItems(slot).multiplyBy(getBaseItemCount()).getProperWhole());
    };

    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
        return insertItem(sequence.getEquivalentBaseItems(stack), stack, simulate);
    };

    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        validateSlotIndex(slot);
        if (!ItemStack.isSameItemSameComponents(stack, sequence.getAllItems().get(slot))) return stack;
        return insertItem(sequence.getEquivalentBaseItems(slot), stack, simulate);
    };

    protected ItemStack insertItem(Fraction equivalentBaseItems, @Nonnull ItemStack stack, boolean simulate) {
        if (equivalentBaseItems == null) return stack;
        Fraction baseItemAmountFraction = equivalentBaseItems.multiplyBy(Fraction.getFraction(stack.getCount(), 1));
        if (baseItemAmountFraction.subtract(getFreeSpace()).doubleValue() > 0d) baseItemAmountFraction = getFreeSpace();
        
        int baseItemAmount = baseItemAmountFraction.getProperWhole();
        int amount = Fraction.getFraction(baseItemAmount, 1).divideBy(equivalentBaseItems).getProperWhole();
        baseItemAmount = equivalentBaseItems.multiplyBy(Fraction.getFraction(amount, 1)).intValue(); // Will be a whole number

        if (!simulate) count += baseItemAmount;
        return stack.copyWithCount(stack.getCount() - amount);
    };

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);

        Fraction fraction = sequence.getEquivalentBaseItems(slot);

        Fraction baseItemAmountFraction = fraction.multiplyBy(Fraction.getFraction(amount, 1));
        if (baseItemAmountFraction.subtract(getBaseItemCount()).doubleValue() > 0d) baseItemAmountFraction = getBaseItemCount();

        int baseItemAmount = baseItemAmountFraction.getProperWhole();
        amount = Fraction.getFraction(baseItemAmount, 1).divideBy(fraction).getProperWhole();
        baseItemAmount = fraction.multiplyBy(Fraction.getFraction(amount, 1)).intValue(); // Will be a whole number

        if (!simulate) count -= baseItemAmount;
        return sequence.getAllItems().get(slot).copyWithCount(amount);
    };

    @Override
    public int getSlotLimit(int slot) {
        validateSlotIndex(slot);
        return sequence.getEquivalentBaseItems(slot).multiplyBy(getCapacity()).getProperWhole();
    };

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, sequence.getAllItems().get(slot));
    };

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= sequence.size()) throw new RuntimeException("Slot " + slot + " not in valid range - [0," + sequence.size() + ")");
    }

    protected Fraction getCapacity() {
        return Fraction.getFraction(capacity, 1);
    };

    protected Fraction getBaseItemCount() {
        return Fraction.getFraction(count, 1);
    };

    protected Fraction getFreeSpace() {
        return getCapacity().subtract(getBaseItemCount());
    };

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Count", count);
        return tag;
    };

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        count = nbt.getInt("Count");
    };


};
