package com.petrolpark.contamination;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.petrolpark.PetrolparkDataComponents;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

public class ItemContamination extends Contamination<Item, ItemStack> {

    public static final String TAG_KEY = "Contamination";

    public static IContamination<?, ?> create(ItemStack stack) {
        if (!Contaminables.ITEM.isContaminableStack(stack)) return IncontaminableContamination.INSTANCE;
        return new ItemContamination(stack);
    };

    public static IContamination<?, ?> get(ItemStack stack) {
        return getDuck(stack).getContamination();
    };

    public static final void perpetuateSingle(final RegistryAccess registries, Stream<ItemStack> inputs, ItemStack output) {
        perpetuate(registries, inputs.map(stack -> stack.copyWithCount(1)), Stream.of(output));
    };

    public static final void perpetuateSingle(final RegistryAccess registries, Stream<ItemStack> inputs, Stream<ItemStack> outputs) {
        perpetuate(registries, inputs.map(stack -> stack.copyWithCount(1)), outputs);
    };

    public static final void perpetuate(final RegistryAccess registries, Stream<ItemStack> inputs, Stream<ItemStack> outputs) {
        IContamination.perpetuate(registries, inputs.dropWhile(ItemStack::isEmpty), outputs, ItemContamination::get);
    };

    protected ItemContamination(ItemStack stack) {
        super(stack);
        orphanContaminants.addAll(stack.getOrDefault(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, new ArrayList<Holder<Contaminant>>()).stream().map(Holder::value).toList());
        for (Contaminant contaminant : orphanContaminants) {
            contaminants.add(contaminant);
            contaminants.addAll(contaminant.getChildren());
        };
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
    public void save(final RegistryAccess registries) {
        stack.set(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, toHolderList(registries));
        getDuck(stack).onContaminationSaved();
        NeoForge.EVENT_BUS.post(new ItemContaminationSavedEvent(stack, this));
    };

    protected static IItemStackDuck getDuck(ItemStack stack) {
        return (IItemStackDuck)(Object)stack;
    };
    
};
