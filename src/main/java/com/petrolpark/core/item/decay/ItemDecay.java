package com.petrolpark.core.item.decay;

import java.util.Optional;
import java.util.function.UnaryOperator;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.core.item.decay.product.IDecayProduct;
import com.petrolpark.core.item.decay.product.NoDecayProduct;
import com.petrolpark.util.DataComponentHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public interface ItemDecay {

    public static long getGameTime() {
        return Petrolpark.runForDist(
            () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                ClientLevel level = mc.level;
                if (level == null) return 0l;
                return level.getGameTime();
            },
            () -> ServerLifecycleHooks.getCurrentServer().overworld()::getGameTime
        );
    };

    /**
     * Get the form of the given ItemStack accounting for the current {@link ItemDecay#getGameTime() game time} and any possible {@link IDecayProduct}s the Item has. This is recursive and will give the ItemStack accurate to the original decay start time.
     * @param stack
     * @return The "true" ItemStack, accounting for that into which it may have decayed
     * @see ItemDecay#checkDecay(ItemStack, UnaryOperator)
     */
    public static ItemStack checkDecay(ItemStack stack) {
        return checkDecay(stack, s -> s);
    };

    /**
     * Get the form of the given ItemStack accounting for the current {@link ItemDecay#getGameTime() game time} and any possible {@link IDecayProduct}s the Item has. This is recursive and will give the ItemStack accurate to the original decay start time.
     * @param stack
     * @param newDecay Use this to get a different decay for the product, beyond any decay added by its prototype or the {@link IDecayProduct}
     * @return The "true" ItemStack, accounting for that into which it may have decayed
     * @see ItemDecay#checkDecay(ItemStack)
     */
    public static ItemStack checkDecay(ItemStack stack, UnaryOperator<ItemStack> newDecay) {
        if (stack.isEmpty()) return stack;
        if (!stack.has(PetrolparkDataComponents.DECAY_TIME) || !stack.has(PetrolparkDataComponents.DECAY_PRODUCT)) return stack;
        Long creationTime = stack.get(PetrolparkDataComponents.DECAY_START_TIME);
        if (creationTime != null) {
            long timeDead = -getRemainingTime(stack, creationTime);
            if (timeDead >= 0) {
                ItemStack copy = copyIgnoringDecay(stack);
                removeAppliedDecay(copy);
                ItemStack product = stack.getOrDefault(PetrolparkDataComponents.DECAY_PRODUCT, NoDecayProduct.INSTANCE).get(copy);
                product.setCount(stack.getCount());
                product = newDecay.apply(product);
                startDecay(product, timeDead);
                return checkDecay(product, newDecay);
            };
        };
        return stack;
    };

    static ItemStack copyIgnoringDecay(ItemStack stack) {
        ItemStack copy = new ItemStack(stack.getItemHolder(), stack.getCount(), stack.getComponentsPatch());
        copy.setPopTime(stack.getPopTime());
        return copy;
    };

    public static ItemStack removeAppliedDecay(ItemStack stack) {
        stack.remove(PetrolparkDataComponents.DECAY_START_TIME);
        DataComponentHelper.revert(stack, PetrolparkDataComponents.DECAY_PRODUCT);
        DataComponentHelper.revert(stack, PetrolparkDataComponents.DECAY_TIME);
        return stack;
    };

    public static long getLifetimeOrNone(ItemStack stack) {
        return stack.getOrDefault(PetrolparkDataComponents.DECAY_TIME, DecayTime.NONE).lifetime();
    };

    public static long getRemainingTime(ItemStack decayingItemStack, long creationTime) {
        return getRemainingTime(getLifetimeOrNone(decayingItemStack), creationTime);
    };

    public static long getRemainingTime(long lifetime, long creationTime) {
        return lifetime + creationTime - getGameTime();
    };

    public static void startDecay(ItemStack stack) {
        startDecay(stack, 0l);
    };

    public static void startDecay(ItemStack stack, long timeElapsed) {
        if (stack.has(PetrolparkDataComponents.DECAY_PRODUCT) && stack.has(PetrolparkDataComponents.DECAY_TIME) && !stack.has(PetrolparkDataComponents.DECAY_START_TIME)) stack.set(PetrolparkDataComponents.DECAY_START_TIME, getGameTime() - timeElapsed);
    };

    public static void extendLifetime(ItemStack decayingItemStack, int additionalLifetime) {
        if (decayingItemStack.has(PetrolparkDataComponents.DECAY_TIME)) {
            Long creationTime = decayingItemStack.get(PetrolparkDataComponents.DECAY_START_TIME);
            if (creationTime == null) return; // Hasn't begun decay
            long remainingTime = getRemainingTime(decayingItemStack, creationTime);
            long newLifetime = Math.max(0, additionalLifetime + remainingTime);
            decayingItemStack.set(PetrolparkDataComponents.DECAY_START_TIME, getGameTime() + newLifetime - getLifetimeOrNone(decayingItemStack));
        };
    };

    @OnlyIn(Dist.CLIENT)
    public static Optional<Component> getTooltip(ItemStack stack) {
        return Optional.ofNullable(stack.get(PetrolparkDataComponents.DECAY_TIME)).map(decayTime -> {
            Long creationTime = stack.get(PetrolparkDataComponents.DECAY_START_TIME);
            long displayedSecondsRemaining;
            if (creationTime != null) {
                long ticksRemaining = ItemDecay.getRemainingTime(decayTime.lifetime(), (long)creationTime);
                displayedSecondsRemaining = ticksRemaining / 20;
            } else {
                displayedSecondsRemaining = decayTime.lifetime() / 20;
            };
            if (displayedSecondsRemaining < 0) displayedSecondsRemaining = 0;
            return Component.translatable(decayTime.translationKey(), String.format("%02d:%02d", displayedSecondsRemaining / 60, displayedSecondsRemaining % 60)).copy().withStyle(ChatFormatting.GRAY);
        });
    };
    
};
