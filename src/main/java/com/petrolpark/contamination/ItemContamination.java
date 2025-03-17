package com.petrolpark.contamination;

import java.util.stream.Stream;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

public class ItemContamination extends ComponentHolderContamination<Item, ItemStack> {

    public static final String TAG_KEY = "Contamination";

    public static IContamination<?, ?> create(ItemStack stack) {
        if (!Contaminables.ITEM.isContaminableStack(stack)) return IncontaminableContamination.INSTANCE;
        return new ItemContamination(stack);
    };

    public static IContamination<?, ?> get(ItemStack stack) {
        return getDuck(stack).getContamination();
    };

    public static final void perpetuateSingle(final HolderLookup.Provider registries, Stream<ItemStack> inputs, ItemStack output) {
        perpetuate(registries, inputs.map(stack -> stack.copyWithCount(1)), Stream.of(output));
    };

    public static final void perpetuateSingle(final HolderLookup.Provider registries, Stream<ItemStack> inputs, Stream<ItemStack> outputs) {
        perpetuate(registries, inputs.map(stack -> stack.copyWithCount(1)), outputs);
    };

    public static final void perpetuate(final HolderLookup.Provider registries, Stream<ItemStack> inputs, Stream<ItemStack> outputs) {
        IContamination.perpetuate(registries, inputs.dropWhile(ItemStack::isEmpty), outputs, ItemContamination::get);
    };

    protected ItemContamination(ItemStack stack) {
        super(stack);
    };

    @Override
    public Contaminable<Item, ItemStack> getContaminable() {
        return Contaminables.ITEM;
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
    public void save(final HolderLookup.Provider registries) {
        super.save(registries);
        NeoForge.EVENT_BUS.post(new ItemContaminationSavedEvent(stack, this));
    };

    protected static IItemStackDuck getDuck(ItemStack stack) {
        return (IItemStackDuck)(Object)stack;
    };
    
};
