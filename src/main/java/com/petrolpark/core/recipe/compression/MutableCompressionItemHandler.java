package com.petrolpark.core.recipe.compression;

import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.DataComponentUtil;

/**
 * A {@link CompressionItemHandler} whose {@link IItemCompressionSequence} is set by the first Item to be added
 * and can changed be by {@link MutableCompressionItemHandler#extractItem(int, int, boolean) emptying}.
 */
public class MutableCompressionItemHandler extends CompressionItemHandler {

    public MutableCompressionItemHandler(int capacity) {
        super(IItemCompressionSequence.EMPTY, capacity);
    };

    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
        return createNewSequenceAndStore(stack, simulate).orElseGet(() -> super.insertItem(stack, simulate));
    };

    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return createNewSequenceAndStore(stack, simulate).orElseGet(() -> super.insertItem(slot, stack, simulate));
    };

    /**
     * Attempts to create a new {@link IItemCompressionSequence} if there isn't already one, and store the given Item Stack in it.
     * @param stack
     * @param simulate
     * @return Optional containing remainder Item Stack if the addition was handled, or empty Optional if it still needs to be
     */
    protected Optional<ItemStack> createNewSequenceAndStore(ItemStack stack, boolean simulate) {
        if (sequence == IItemCompressionSequence.EMPTY) {
            Optional<IItemCompressionSequence> newSequence = ItemCompressionManager.getSequence(stack).map(onNewSequence(simulate));
            if (newSequence.isPresent()) {
                sequence = newSequence.get();
                ItemStack remainder = super.insertItem(stack, simulate);
                if (simulate) sequence = IItemCompressionSequence.EMPTY; // Reset the sequence if this was only a simulated insertion 
                return Optional.of(remainder);
            } else {
                return Optional.of(stack);
            }
        };
        return Optional.empty();
    };

    /**
     * Allows filtering and any other response to the new {@link IItemCompressionSequence} once it is (going to be) set.
     * Called when reading this handler from NBT and when setting the new 
     * @param simulate
     * @return Operator on the newly set {@link IItemCompressionSequence}
     */
    protected UnaryOperator<IItemCompressionSequence> onNewSequence(boolean simulate) {
        return sequence -> sequence;
    };

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extracted = super.extractItem(slot, amount, simulate);
        if (!simulate && count == 0) sequence = IItemCompressionSequence.EMPTY;
        return extracted;
    };

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        if (sequence != IItemCompressionSequence.EMPTY) tag.put("Item", DataComponentUtil.wrapEncodingExceptions(sequence.getBaseItem(), ItemStack.SINGLE_ITEM_CODEC, provider));
        return tag;
    };

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        Optional<ItemStack> stack = ItemStack.parse(provider, nbt.getCompound("Item"));
        sequence = stack.flatMap(ItemCompressionManager::getSequence)
            .map(onNewSequence(false))
            .orElse(stack.<IItemCompressionSequence>map(NoItemCompressionSequence::new)
                .orElse(IItemCompressionSequence.EMPTY)
            );
    };
    
};
