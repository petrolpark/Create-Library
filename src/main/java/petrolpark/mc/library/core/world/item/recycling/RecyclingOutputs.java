package petrolpark.mc.library.core.world.item.recycling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import petrolpark.mc.library.util.BigItemStack;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenCustomHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

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
    public double expectationMultiplier = 1d;

    public RecyclingOutputs() {

    };

    public RecyclingOutputs(ItemStack stack) {
        this(Collections.singleton(new RecyclingOutput(stack)));
    };

    public RecyclingOutputs(Collection<RecyclingOutput> outputs) {
        super(outputs);
    };

    public double getExpectationMultiplier() {
        return expectationMultiplier;
    };

    public boolean hasOutputs() {
        return !isEmpty();
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
     */
    public void splitAll(double proportion, Consumer<RecyclingOutput> modification) {
        splitAll(proportion, modification, o -> {});
    };

    /**
     * Splits every existing {@link RecyclingOutput} into two.
     * @param proportion The proportion of each Output to be modified
     * @param modification The modification to apply to that proportion of the Output
     * @param remainderModification The modification to apply to the remainder of the Output
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

    /**
     * Reduces two {@link RecyclingOutputs} to their minimum shared {@link RecyclingOutput}s.
     * If an Item has multiple Recipes (not just one Recipe with complex Ingredients), this gives only the Items used in both Recipes.
     * For instance, a Copper Block can craft four Copper Grates with a Stonecutter but only one with a Crafting Table, so this returns the only shared Ingredient, which is a single Copper Block.
     * In a lot of cases this will return {@link RecyclingOutputs#empty()}.
     */
    public static final RecyclingOutputs intersect(RecyclingOutputs outputs1, RecyclingOutputs outputs2) {
        if (outputs1.isEmpty()) return outputs1;
        if (outputs2.isEmpty()) return outputs2;
        final Object2DoubleMap<ItemStack> stacks = new Object2DoubleOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG);
        outputs1.forEach(output -> stacks.put(output.getItem(), output.getExpectedCount() * outputs1.getExpectationMultiplier()));
        final Object2DoubleMap<ItemStack> result = new Object2DoubleOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG);
        outputs2.forEach(output -> {
            double amount = stacks.getDouble(output.getItem());
            if (amount != 0d) result.put(output.getItem(), Math.min(amount, output.getExpectedCount() * outputs2.getExpectationMultiplier()));
        });
        if (result.isEmpty()) return empty();
        return result.object2DoubleEntrySet().stream().map(entry -> new RecyclingOutput(entry.getKey(), entry.getDoubleValue()))
            .collect(RecyclingOutputs::new, RecyclingOutputs::add, RecyclingOutputs::addOther);
    };

    public List<ItemStack> getMaxPossibleStacks() {
        return stream()
            .map(output -> output.getMaxStack(expectationMultiplier))
            .map(BigItemStack::getAsStacks)
            .flatMap(List::stream)
            .toList();
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

    public static final Collector<RecyclingOutputs, RecyclingOutputs, RecyclingOutputs> COLLECTOR = new Collector<RecyclingOutputs,RecyclingOutputs,RecyclingOutputs>() {

        @Override
        public Supplier<RecyclingOutputs> supplier() {
            return RecyclingOutputs::empty;
        };

        @Override
        public BiConsumer<RecyclingOutputs, RecyclingOutputs> accumulator() {
            return RecyclingOutputs::addOther;
        };

        @Override
        public BinaryOperator<RecyclingOutputs> combiner() {
            return RecyclingOutputs::addOther;
        };

        @Override
        public Function<RecyclingOutputs, RecyclingOutputs> finisher() {
            return Function.identity();
        };

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return Set.of(Collector.Characteristics.IDENTITY_FINISH);
        };
        
    };
};
