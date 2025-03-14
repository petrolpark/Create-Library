package com.petrolpark.item.decay;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponents;

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
            Long creationTime = stack.get(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME);
            if (creationTime != null) {
                long timeDead = -getRemainingTime(item, stack, creationTime);
                if (timeDead >= 0) {
                    ItemStack product = item.getDecayProduct(stack);
                    product.setCount(stack.getCount());
                    product.set(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, stack.get(PetrolparkDataComponents.ORPHAN_CONTAMINANTS)); // Propagate Contaminants
                    startDecay(product, timeDead);
                    return checkDecay(product);
                };
            };
        };
        return stack;
    };

    public static long getRemainingTime(IDecayingItem decayingItem, ItemStack decayingItemStack, long creationTime) {
        return decayingItem.getLifetime(decayingItemStack) + creationTime - Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime();
    };

    public static void startDecay(ItemStack stack) {
        startDecay(stack, 0l);
    };

    public static void startDecay(ItemStack stack, long timeElapsed) {
        if (stack.getItem() instanceof IDecayingItem) {
            if (!stack.has(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME)) stack.set(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME, Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime() - timeElapsed);
        };
    };

    public static void extendLifetime(ItemStack decayingItemStack, int additionalLifetime) {
        if (decayingItemStack.getItem() instanceof IDecayingItem item) {
            Long creationTime = decayingItemStack.get(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME);
            if (creationTime == null) return; // No lifetime to extend
            long remainingTime = getRemainingTime(item, decayingItemStack, creationTime);
            long newLifetime = Math.max(0, additionalLifetime + remainingTime);
            decayingItemStack.set(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME, Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime() + newLifetime - item.getLifetime(decayingItemStack));
        };
        
    };
    
};
