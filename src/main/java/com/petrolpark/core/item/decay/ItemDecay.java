package com.petrolpark.core.item.decay;

import java.util.Optional;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.core.item.decay.product.NoDecayProduct;

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

    public static ItemStack checkDecay(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (!stack.has(PetrolparkDataComponents.DECAY_TIME) || !stack.has(PetrolparkDataComponents.DECAY_PRODUCT)) return stack;
        Long creationTime = stack.get(PetrolparkDataComponents.DECAY_START_TIME);
        if (creationTime != null) {
            long timeDead = -getRemainingTime(stack, creationTime);
            if (timeDead >= 0) {
                ItemStack product = stack.getOrDefault(PetrolparkDataComponents.DECAY_PRODUCT, NoDecayProduct.INSTANCE).get(stack.copy());
                product.setCount(stack.getCount());
                product.set(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, stack.get(PetrolparkDataComponents.ORPHAN_CONTAMINANTS)); // Propagate Contaminants
                startDecay(product, timeDead);
                return checkDecay(product);
            };
        };
        return stack;
    };

    public static long getLifetimeOrNone(ItemStack stack) {
        return stack.getOrDefault(PetrolparkDataComponents.DECAY_TIME, DecayTime.NONE).lifetime();
    };

    public static long getRemainingTime(ItemStack decayingItemStack, long creationTime) {
        return getReminaingTime(decayingItemStack.getOrDefault(PetrolparkDataComponents.DECAY_TIME, DecayTime.NONE).lifetime(), creationTime);
    };

    public static long getReminaingTime(long lifetime, long creationTime) {
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
            if (creationTime == null) return; // Hasn't began decay
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
                long ticksRemaining = ItemDecay.getReminaingTime(decayTime.lifetime(), (long)creationTime);
                displayedSecondsRemaining = ticksRemaining / 20;
            } else {
                displayedSecondsRemaining = decayTime.lifetime() / 20;
            };
            if (displayedSecondsRemaining < 0) displayedSecondsRemaining = 0;
            return Component.translatable(decayTime.translationKey(), String.format("%02d:%02d", displayedSecondsRemaining / 60, displayedSecondsRemaining % 60)).copy().withStyle(ChatFormatting.GRAY);
        });
    };
    
};
