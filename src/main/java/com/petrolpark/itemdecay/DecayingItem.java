package com.petrolpark.itemdecay;

import java.util.List;
import java.util.function.Consumer;

import com.petrolpark.Petrolpark;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public abstract class DecayingItem extends Item implements IDecayingItem {

    public DecayingItem(Properties properties) {
        super(properties);
    };

    @Override
    public void appendHoverText(ItemStack stack, Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, tooltip, pIsAdvanced);
        CompoundTag tag = stack.getOrCreateTag();
        long displayedSecondsRemaining;
        if (tag.contains("CreationTime", Tag.TAG_LONG)) {
            long ticksRemaining = IDecayingItem.getRemainingTime(this, stack, tag);
            displayedSecondsRemaining = ticksRemaining / 20;
        } else {
            displayedSecondsRemaining = getLifetime(stack) / 20;
        };
        if (displayedSecondsRemaining < 0) displayedSecondsRemaining = 0;
        tooltip.add(Component.translatable(getDecayTimeTranslationKey(stack), String.format("%02d:%02d", displayedSecondsRemaining / 60, displayedSecondsRemaining % 60)).copy().withStyle(ChatFormatting.GRAY));
    };

    @Override
    public void onCraftedBy(ItemStack stack, Level pLevel, Player pPlayer) {
        IDecayingItem.startDecay(stack, 0);
    };

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        ItemStack trueStack = IDecayingItem.checkDecay(stack);
        ItemStack otherTrueStack = IDecayingItem.checkDecay(other);
        if (stack == trueStack && other == otherTrueStack && stack.is(other.getItem()) && stack.hasTag() && other.hasTag() && areDecayTimesCombineable(stack, other)) {
            CompoundTag tag = stack.getOrCreateTag();
            CompoundTag otherTag = other.getOrCreateTag();
            if (tag.contains("CreationTime", Tag.TAG_LONG) && otherTag.contains("CreationTime", Tag.TAG_LONG)) {
                int transferred = action == ClickAction.PRIMARY ? Math.min(stack.getMaxStackSize() - stack.getCount(), other.getCount()) : 1;
                long totalTime = (stack.getCount() * IDecayingItem.getRemainingTime(this, stack, tag)) + (transferred * IDecayingItem.getRemainingTime(this, other, otherTag));
                
                stack.grow(transferred);
                tag.putLong("CreationTime", Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime() + (totalTime / stack.getCount()) - getLifetime(stack));
                other.shrink(transferred);
                return true;
            };
        };
        return false;
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new DecayingItemRenderer()));
    };
    
};
