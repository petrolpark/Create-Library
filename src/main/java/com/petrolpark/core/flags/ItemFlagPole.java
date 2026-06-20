package com.petrolpark.core.flags;

import java.util.stream.Stream;

import com.petrolpark.core.item.IItemStackDuck;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

public class ItemFlagPole extends ComponentHolderFlagPole<Item, ItemStack> {

    public static IFlagPole<?, ?> create(ItemStack stack) {
        if (!Flaggables.ITEM.isFlaggableStack(stack)) return UnflaggableFlagPole.INSTANCE;
        return new ItemFlagPole(stack);
    };

    public static IFlagPole<?, ?> get(ItemStack stack) {
        return getDuck(stack).getFlags();
    };

    public static final void perpetuateSingle(Stream<ItemStack> inputs, ItemStack output) {
        perpetuate(inputs.map(stack -> stack.copyWithCount(1)), Stream.of(output));
    };

    public static final void perpetuateSingle(Stream<ItemStack> inputs, Stream<ItemStack> outputs) {
        perpetuate(inputs.map(stack -> stack.copyWithCount(1)), outputs);
    };

    public static final void perpetuate(Stream<ItemStack> inputs, Stream<ItemStack> outputs) {
        IFlagPole.perpetuate(inputs.dropWhile(ItemStack::isEmpty), outputs, ItemFlagPole::get);
    };

    protected ItemFlagPole(ItemStack stack) {
        super(stack);
    };

    @Override
    public Flaggable<Item, ItemStack> getFlaggable() {
        return Flaggables.ITEM;
    };

    @Override
    public Item getType() {
        return stack.getItem();
    };

    @Override
    public double getAmount() {
        return stack.getCount();
    };

    @Override
    public void save() {
        super.save();
        getDuck(stack).onFlagsSaved();
        NeoForge.EVENT_BUS.post(new ItemFlagPoleSavedEvent(stack, this));
    };

    protected static IItemStackDuck getDuck(ItemStack stack) {
        return (IItemStackDuck)(Object)stack;
    };
    
};
