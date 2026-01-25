package com.petrolpark.compat.create.core.chainconveyor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;

public abstract class ChainConveyorItemEvent extends Event implements ICancellableEvent {

    public static final boolean canAdd(Level level, ItemStack stack, ChainConveyorBlockEntity chainConveyor, BlockPos chainConveyorConnection, float chainConveyorPosition, boolean simulate) {
        final ChainConveyorItemEvent.Add event = new ChainConveyorItemEvent.Add(level, stack, chainConveyor, chainConveyorConnection, chainConveyorPosition, simulate);
        NeoForge.EVENT_BUS.post(event);
        if (event.isAllowed()) {
            final ItemStack addedStack = event.transformedStack.copyWithCount(1);
            final ChainConveyorPackage box = new ChainConveyorPackage(chainConveyorPosition, addedStack);
            if (!simulate) {
                if (chainConveyorConnection == null)
                    chainConveyor.addLoopingPackage(box);
                else
                    chainConveyor.addTravellingPackage(box, chainConveyorConnection);
                event.onSuccess.forEach(consumer -> consumer.accept(addedStack));
            };
            return true;
        };
        return false;
    };

    public static final ChainConveyorItemEvent.Add getAdded(Level level, ItemStack stack, ChainConveyorBlockEntity chainConveyor, BlockPos chainConveyorConnection, float chainConveyorPosition, boolean simulate) {
        final ChainConveyorItemEvent.Add event = new ChainConveyorItemEvent.Add(level, stack, chainConveyor, chainConveyorConnection, chainConveyorPosition, simulate);
        NeoForge.EVENT_BUS.post(event);
        if (event.isAllowed()) {
            event.transformedStack.setCount(1);
            event.onSuccess.forEach(consumer -> consumer.accept(event.transformedStack));
        };
        return event;
    };

    public static final ChainConveyorItemEvent.Remove getRemoved(Level level, ChainConveyorBlockEntity chainConveyor, BlockPos chainConveyorConnection, ChainConveyorPackage box, boolean simulate) {
        return getRemoved(level, box.item, chainConveyor, chainConveyorConnection, box.chainPosition, simulate);
    };

    public static final ChainConveyorItemEvent.Remove getRemoved(Level level, ItemStack stack, ChainConveyorBlockEntity chainConveyor, BlockPos chainConveyorConnection, float chainConveyorPosition, boolean simulate) {
        final ChainConveyorItemEvent.Remove event = new ChainConveyorItemEvent.Remove(level, stack, chainConveyor, chainConveyorConnection, chainConveyorPosition, simulate);
        NeoForge.EVENT_BUS.post(event);
        if (event.isAllowed() && !simulate) {
            final ItemStack removedStack = event.transformedStack;
            event.onSuccess.forEach(consumer -> consumer.accept(removedStack));
        };
        return event;
    };

    public static final boolean canAddClient(Level level, ItemStack stack) {
        final ChainConveyorItemEvent.AddClient event = new ChainConveyorItemEvent.AddClient(level, stack);
        NeoForge.EVENT_BUS.post(event);
        return event.canAdd;
    };
    
    public final Level level;
    protected final ItemStack originalStack;
    public final ChainConveyorBlockEntity chainConveyor;
    @Nullable public final BlockPos chainConveyorConnection;
    public final float chainConveyorPosition;
    public final boolean simulate;

    protected ItemStack transformedStack = ItemStack.EMPTY;
    protected List<Consumer<ItemStack>> onSuccess = new ArrayList<>();

    protected ChainConveyorItemEvent(Level level, ItemStack stack, ChainConveyorBlockEntity chainConveyor, BlockPos chainConveyorConnection, float chainConveyorPosition, boolean simulate) {
        this.level = level;
        this.originalStack = stack;
        this.chainConveyor = chainConveyor;
        this.chainConveyorConnection = chainConveyorConnection;
        this.chainConveyorPosition = chainConveyorPosition;
        this.simulate = simulate;
    };

    public ItemStack getStack() {
        return transformedStack.isEmpty() ? originalStack : transformedStack;
    };

    public void allow() {
        setTransformedStack(originalStack);
    };

    public void setTransformedStack(ItemStack stack) {
        transformedStack = stack;
    };

    public void onSuccess(Consumer<ItemStack> consumer) {
        onSuccess.add(consumer);
    };

    @Override
    public void setCanceled(boolean canceled) {
        ICancellableEvent.super.setCanceled(canceled);
        if (isCanceled()) transformedStack = ItemStack.EMPTY;
    };

    public boolean isAllowed() {
        return !transformedStack.isEmpty();
    };

    public static class Add extends ChainConveyorItemEvent {

        protected Add(Level level, ItemStack stack, ChainConveyorBlockEntity chainConveyor, BlockPos chainConveyorConnection, float chainConveyorPosition, boolean simulate) {
            super(level, stack, chainConveyor, chainConveyorConnection, chainConveyorPosition, simulate);
        };

    };

    public static class Remove extends ChainConveyorItemEvent {

        protected Remove(Level level, ItemStack stack, ChainConveyorBlockEntity chainConveyor, BlockPos chainConveyorConnection, float chainConveyorPosition, boolean simulate) {
            super(level, stack, chainConveyor, chainConveyorConnection, chainConveyorPosition, simulate);
        };

    };

    public static class AddClient extends Event implements ICancellableEvent {

        public final Level level;
        public final ItemStack stack;
        protected boolean canAdd = false;

        protected AddClient(Level level, ItemStack stack) {
            this.level = level;
            this.stack = stack;
        };

        public void set(boolean canAdd) {
            this.canAdd = canAdd;
        };

        public void or(boolean canAdd) {
            this.canAdd |= canAdd;
        };

        @Override
        public void setCanceled(boolean canceled) {
            ICancellableEvent.super.setCanceled(canceled);
            if (isCanceled()) canAdd = false;
        }
    };
};