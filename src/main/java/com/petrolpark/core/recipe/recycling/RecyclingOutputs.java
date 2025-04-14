package com.petrolpark.core.recipe.recycling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.petrolpark.util.BigItemStack;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public class RecyclingOutputs extends LinkedList<RecyclingOutput> {

    public static final RecyclingOutputs empty() {
        return new RecyclingOutputs(Collections.emptyList());
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, RecyclingOutputs> STREAM_CODEC = StreamCodec.composite(
        RecyclingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), output -> (List<RecyclingOutput>)output,
        ByteBufCodecs.DOUBLE, RecyclingOutputs::getExpectationMultiplier,
        (list, multiplier) -> new RecyclingOutputs(list).multiplyAll(multiplier)
    );
    
    /**
     * Factor by which the expected amounts of all {@link RecyclingOutput}s are multiplied when {@link RecyclingOutputs#rollStacks actually determining the output}.
     * Change this to avoid having to iterate over every single individual output.
     */
    public double expectationMultiplier = 1f;

    public RecyclingOutputs(ItemStack stack) {
        this(Collections.singleton(new RecyclingOutput(stack)));
    };

    public RecyclingOutputs(Collection<RecyclingOutput> outputs) {
        super(outputs);
    };

    public double getExpectationMultiplier() {
        return expectationMultiplier;
    };

    @Override
    public void add(int index, RecyclingOutput output) {
        add(output);
    };

    @Override
    public boolean add(RecyclingOutput newOutput) {
        int i = 0;
        for (RecyclingOutput output : this) {
            if (ItemStack.isSameItemSameComponents(output.getItem(), newOutput.getItem())) {
                output.expectedCount += newOutput.expectedCount;
                return true;
            };
            if (newOutput.compareTo(output) < 0) break; // Insert sorted
            i++;
        };
        super.add(i, newOutput);
        return true;
    };

    public RecyclingOutputs addOther(RecyclingOutputs outputs) {
        addAll(outputs);
        return this;
    };

    @Override
    public boolean addAll(Collection<? extends RecyclingOutput> collection) {
        if (collection instanceof RecyclingOutputs outputs) {
            outputs.forEach(output -> add(output.copy().multiply(outputs.getExpectationMultiplier())));
            return true;
        };
        return super.addAll(collection);
    };

    public RecyclingOutputs multiplyAll(double factor) {
        expectationMultiplier *= factor;
        return this;
    };

    /**
     * Splits every existing {@link RecyclingOutput} into two.
     * @param proportion The proportion of each Output to be modified
     * @param modification The modification to apply to that proportion of the Output
     * @see RecyclingOutputs#splitAll(float, Consumer, Consumer)
     */
    public void splitAll(double proportion, Consumer<RecyclingOutput> modification) {
        splitAll(proportion, modification, o -> {});
    };

    /**
     * Splits every existing {@link RecyclingOutput} into two.
     * @param proportion The proportion of each Output to be modified
     * @param modification The modification to apply to that proportion of the Output
     * @param remainderModification The modification to apply to the remainder of the Output
     * @see RecyclingOutputs#splitAll(float, Consumer)
     */
    public void splitAll(double proportion, Consumer<RecyclingOutput> modification, Consumer<RecyclingOutput> remainderModification) {
        List<RecyclingOutput> existingOutputs = new ArrayList<>(this);
        for (RecyclingOutput existing : existingOutputs) {
            RecyclingOutput split = existing.copy().multiply(proportion);
            modification.accept(split);
            add(split);
            existing.multiply(1f - proportion);
            remainderModification.accept(existing);
        };
    };

    public List<ItemStack> rollStacks(RandomSource randomSource) {
        return stream()
            .map(output -> output.rollStack(expectationMultiplier, randomSource))
            .map(BigItemStack::getAsStacks)
            .flatMap(List::stream)
            .toList();
    };

    public RecyclingOutputs copy() {
        return new RecyclingOutputs(this).multiplyAll(expectationMultiplier);
    };
    
    @Override
    public boolean equals(Object o) {
        return o instanceof RecyclingOutputs outputs && expectationMultiplier == outputs.expectationMultiplier && super.equals(outputs);
    };
};
