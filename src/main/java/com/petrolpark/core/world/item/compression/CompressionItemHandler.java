package com.petrolpark.core.world.item.compression;

import javax.annotation.Nonnull;

import org.apache.commons.math3.fraction.BigFraction;

import com.petrolpark.util.MathsHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;

public class CompressionItemHandler implements IItemHandler, INBTSerializable<CompoundTag> {

    /**
     * Capacity of {@link IItemCompressionSequence#getBaseItem() base Items}.
     */
    protected final int capacity;
    protected final BigFraction capacityFraction;

    /**
     * Number of {@link IItemCompressionSequence#getBaseItem() base Items}.
     */
    protected long count = 0;

    protected IItemCompressionSequence sequence;

    public CompressionItemHandler(IItemCompressionSequence sequence, int capacity) {
        this.sequence = sequence;
        this.capacity = capacity;
        capacityFraction = BigFraction.getReducedFraction(capacity, 1);
    };

    @Override
    public int getSlots() {
        return sequence.size();
    };

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return sequence.getAllItems().get(slot).copyWithCount(MathsHelper.properWholeInt(sequence.getEquivalentBaseItems(slot).multiply(getBaseItemCount())));
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

    protected ItemStack insertItem(BigFraction equivalentBaseItems, @Nonnull ItemStack stack, boolean simulate) {
        if (equivalentBaseItems == null) return stack;
        BigFraction baseItemAmountFraction = equivalentBaseItems.multiply(BigFraction.getReducedFraction(stack.getCount(), 1));
        if (baseItemAmountFraction.subtract(getFreeSpace()).doubleValue() > 0d) baseItemAmountFraction = getFreeSpace();
        
        long baseItemAmount = MathsHelper.properWhole(baseItemAmountFraction);
        int amount = MathsHelper.properWholeInt(new BigFraction(baseItemAmount, 1l).divide(equivalentBaseItems));
        baseItemAmount = equivalentBaseItems.multiply(BigFraction.getReducedFraction(amount, 1)).longValue(); // Will be a whole number

        if (!simulate) count += baseItemAmount;
        return stack.copyWithCount(stack.getCount() - amount);
    };

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);

        BigFraction fraction = sequence.getEquivalentBaseItems(slot);

        BigFraction baseItemAmountFraction = fraction.multiply(BigFraction.getReducedFraction(amount, 1));
        if (baseItemAmountFraction.subtract(getBaseItemCount()).doubleValue() > 0d) baseItemAmountFraction = getBaseItemCount();

        int baseItemAmount = MathsHelper.properWholeInt(baseItemAmountFraction);
        amount = MathsHelper.properWholeInt(BigFraction.getReducedFraction(baseItemAmount, 1).divide(fraction));
        baseItemAmount = fraction.multiply(BigFraction.getReducedFraction(amount, 1)).intValue(); // Will be a whole number

        if (!simulate) count -= baseItemAmount;
        return sequence.getAllItems().get(slot).copyWithCount(amount);
    };

    @Override
    public int getSlotLimit(int slot) {
        validateSlotIndex(slot);
        return MathsHelper.properWholeInt(sequence.getEquivalentBaseItems(slot).multiply(getCapacity()));
    };

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, sequence.getAllItems().get(slot));
    };

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= sequence.size()) throw new RuntimeException("Slot " + slot + " not in valid range - [0," + sequence.size() + ")");
    }

    protected BigFraction getCapacity() {
        return capacityFraction;
    };

    protected BigFraction getBaseItemCount() {
        return new BigFraction(count, 1l);
    };

    protected BigFraction getFreeSpace() {
        return getCapacity().subtract(getBaseItemCount());
    };

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Count", count);
        return tag;
    };

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        count = nbt.getLong("Count");
    };

    
};
