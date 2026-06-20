package com.petrolpark.core.world.item.compression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.math3.fraction.BigFraction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.ItemStackMap;

public class FinishableMapItemCompressionSequence implements IItemCompressionSequence {

    protected boolean finished = false;
    protected final ItemStack baseItem;

    protected final Map<ItemStack, BigFraction> fractionsByStack = ItemStackMap.createTypeAndTagMap();
    protected final List<BigFraction> fractionsByIndex = new ArrayList<>();

    protected final List<ItemStack> allItems = new ArrayList<>();
    protected final List<IItemCompression> compressions = new ArrayList<>();
    protected Optional<CompressedBlock> baseBlock = Optional.empty();

    public FinishableMapItemCompressionSequence(ItemStack baseItem) {
        this.baseItem = baseItem;
        fractionsByIndex.add(BigFraction.ONE);
        allItems.add(baseItem);
        checkToAddBaseBlock(baseItem);
    };

    /**
     * @param compression
     * @return Whether the Compression could be successfully added (didn't introduce a circular sequence)
     */
    public boolean add(IItemCompression compression) {
        if (finished) throw new IllegalStateException("Cannot add Items to sequence after it is finished building.");
        ItemStack newItem = compression.result().copyWithCount(1);
        if (fractionsByStack.containsKey(newItem)) return false;
        ItemStack lastItem = compressions.size() == 0 ? baseItem.copyWithCount(1) : compressions.get(compressions.size() - 1).result().copyWithCount(1);
        BigFraction fraction = fractionsByIndex.get(fractionsByIndex.size() - 1).multiply(BigFraction.getReducedFraction(compression.count(), compression.result().getCount()));
        fractionsByStack.put(lastItem, fraction);
        fractionsByIndex.add(fraction);
        allItems.add(newItem);
        compressions.add(compression);
        checkToAddBaseBlock(newItem);
        return true;
    };

    @Override
    public ItemStack getBaseItem() {
        return baseItem;
    };

    @Override
    public List<ItemStack> getKnownItems() {
        return allItems;
    };

    @Override
    public List<ItemStack> getAllItems() {
        if (!finished) throw new IllegalStateException("Cannot access Item Stacks of sequence before building it has been finished.");
        return allItems;
    };

    @Override
    public List<IItemCompression> getAllCompressions() {
        if (!finished) throw new IllegalStateException("Cannot access Compressions of sequence before building it has been finished.");
        return compressions;
    };

    @Override
    public BigFraction getEquivalentBaseItems(ItemStack stack) {
        BigFraction fraction = fractionsByStack.get(stack);
        if (fraction == null) return null;
        return fraction.multiply(BigFraction.getReducedFraction(stack.getCount(), 1));
    };

    @Override
    public double getEquivalentBaseItems(ItemStack stack, double count) {
        BigFraction fraction = fractionsByStack.get(stack);
        if (fraction == null) return 0d;
        return fraction.doubleValue() * count;
    };

    @Override
    public BigFraction getEquivalentBaseItems(int item) {
        if (item < 0 || item >= fractionsByIndex.size()) return null;
       return fractionsByIndex.get(item);
    };

    @Override
    public Optional<CompressedBlock> getBaseBlock() {
        if (!finished) throw new IllegalStateException("Cannot access base Block of sequence before building it has been finished.");
        return baseBlock;
    };

    protected void checkToAddBaseBlock(ItemStack stack) {
        if (baseBlock.isEmpty() && stack.getItem() instanceof BlockItem blockItem) baseBlock = Optional.of(new CompressedBlock(blockItem.getBlock(), stack));
    };

    public FinishableMapItemCompressionSequence finish() {
        finished = true;
        return this;
    };
    
};
