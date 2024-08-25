package com.petrolpark.itemdecay;

import com.petrolpark.Petrolpark;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public interface IDecayingItem {

    /**
     * Get the Item Stack into which the decaying Item Stack decays.
     * @param stack
     */
    public ItemStack getDecayProduct(ItemStack stack);

    /**
     * Get the total lifetime in ticks of an Item Stack, not considering the current time it has been alive.
     * @param stack
     */
    public long getLifetime(ItemStack stack);

    public default boolean areDecayTimesCombineable(ItemStack stack1, ItemStack stack2) {
        return true;
    };

    public default String getDecayTimeTranslationKey(ItemStack stack) {
        return "item.petrolpark.decaying_item.remaining";
    };

    public static ItemStack checkDecay(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (stack.getItem() instanceof IDecayingItem item) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("CreationTime", Tag.TAG_LONG)) {
                long timeDead = -getRemainingTime(item, stack, tag);
                if (timeDead >= 0) {
                    ItemStack product = item.getDecayProduct(stack);
                    product.setCount(stack.getCount());
                    startDecay(product, timeDead);
                    return checkDecay(product);
                };
            };
        };
        return stack;
    };

    public static long getRemainingTime(IDecayingItem decayingItem, ItemStack decayingItemStack, CompoundTag decayingItemTag) {
        return decayingItem.getLifetime(decayingItemStack) + decayingItemTag.getLong("CreationTime") - Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime();
    };

    public static void startDecay(ItemStack stack, long timeElapsed) {
        if (stack.getItem() instanceof IDecayingItem item) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("CreationTime", Tag.TAG_LONG)) tag.putLong("CreationTime", Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime() - timeElapsed);
        };
    };

    public static void extendLifetime(ItemStack decayingItemStack, int additionalLifetime) {
        if (decayingItemStack.getItem() instanceof IDecayingItem item) {
            CompoundTag tag = decayingItemStack.getOrCreateTag();
            long remainingTime = getRemainingTime(item, decayingItemStack, tag);
            long newLifetime = Math.max(0, additionalLifetime + remainingTime);
            tag.putLong("CreationTime", Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime() + newLifetime - item.getLifetime(decayingItemStack));
        };
        
    };
    
};
