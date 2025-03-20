package com.petrolpark.core.item.decay;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponents;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public abstract class DecayingItem extends Item implements IDecayingItem {

    public DecayingItem(Properties properties) {
        super(properties);
    };

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, context, tooltip, pIsAdvanced);
        Long creationTime = stack.get(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME);
        long displayedSecondsRemaining;
        if (creationTime != null) {
            long ticksRemaining = IDecayingItem.getRemainingTime(this, stack, creationTime);
            displayedSecondsRemaining = ticksRemaining / 20;
        } else {
            displayedSecondsRemaining = getLifetime(stack) / 20;
        };
        if (displayedSecondsRemaining < 0) displayedSecondsRemaining = 0;
        tooltip.add(Component.translatable(getDecayTimeTranslationKey(stack), String.format("%02d:%02d", displayedSecondsRemaining / 60, displayedSecondsRemaining % 60)).copy().withStyle(ChatFormatting.GRAY));
    };

    @Override
    public void onCraftedBy(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Player player) {
        IDecayingItem.startDecay(stack, 0);
    };

    @Override
    public boolean overrideOtherStackedOnMe(@Nonnull ItemStack stack, @Nonnull ItemStack other, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player, @Nonnull SlotAccess access) {
        ItemStack trueStack = IDecayingItem.checkDecay(stack);
        ItemStack otherTrueStack = IDecayingItem.checkDecay(other);
        if (stack == trueStack && other == otherTrueStack && areDecayTimesCombineable(stack, other)) {
            Long creationTime = stack.get(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME);
            Long otherCreationTime = other.get(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME);
            if (creationTime != null && otherCreationTime != null) {
                int transferred = action == ClickAction.PRIMARY ? Math.min(stack.getMaxStackSize() - stack.getCount(), other.getCount()) : 1;
                long totalTime = (stack.getCount() * IDecayingItem.getRemainingTime(this, stack, creationTime)) + (transferred * IDecayingItem.getRemainingTime(this, other, otherCreationTime));
                stack.grow(transferred);
                stack.set(PetrolparkDataComponents.DECAYING_ITEM_CREATION_TIME, Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime() + (totalTime / stack.getCount()) - getLifetime(stack));
                other.shrink(transferred);
                return true;
            };
        };
        return false;
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new DecayingItemRenderer()));
    };
    
};
