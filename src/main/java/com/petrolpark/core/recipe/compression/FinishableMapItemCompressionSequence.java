package com.petrolpark.core.recipe.compression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.math.Fraction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.ItemStackMap;

public class FinishableMapItemCompressionSequence implements IItemCompressionSequence {

    protected boolean finished = false;
    protected final ItemStack baseItem;

    protected final Map<ItemStack, Fraction> fractionsByStack = ItemStackMap.createTypeAndTagMap();
    protected final List<Fraction> fractionsByIndex = new ArrayList<>();

    protected final List<ItemStack> allItems = new ArrayList<>();
    protected final List<IItemCompression> compressions = new ArrayList<>();
    protected Optional<CompressedBlock> baseBlock = Optional.empty();

    public FinishableMapItemCompressionSequence(ItemStack baseItem) {
        this.baseItem = baseItem;
        fractionsByIndex.add(Fraction.ONE);
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
        Fraction fraction = fractionsByIndex.get(fractionsByIndex.size() - 1).multiplyBy(Fraction.getFraction(compression.count(), compression.result().getCount()));
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
    public Fraction getEquivalentBaseItems(ItemStack stack) {
        return fractionsByStack.get(stack);
    };

    @Override
    public Fraction getEquivalentBaseItems(int item) {
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
