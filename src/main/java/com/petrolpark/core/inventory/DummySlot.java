package com.petrolpark.core.inventory;

import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class DummySlot extends Slot {

    public DummySlot(DummyContainer container, int slot) {
        super(container, slot, 0, 0);
    };

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    };

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    };

    @Override
    public boolean hasItem() {
        return false;
    };

    @Override
    public int getMaxStackSize() {
        return 0;
    };

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    };

    @Override
    public boolean mayPickup(@Nonnull Player player) {
        return false;
    };

    @Override
    public boolean isActive() {
        return false;
    };

    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, @Nonnull Player player) {
        return Optional.empty();
    };

    @Override
    public ItemStack safeTake(int count, int decrement, @Nonnull Player player) {
        return ItemStack.EMPTY;
    };

    @Override
    public ItemStack safeInsert(@Nonnull ItemStack stack, int increment) {
        return stack;
    };

    @Override
    public boolean isHighlightable() {
        return false;
    };

    @Override
    public boolean isFake() {
        return true;
    };
    
};
