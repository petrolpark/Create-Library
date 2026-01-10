package com.petrolpark.core.recipe.recycling;

import java.util.List;

import com.petrolpark.core.contamination.Contamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.recipe.compression.ItemCompressionManager;
import com.petrolpark.util.BigItemStack;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A modification to the {@link RecyclingOutputs} of a {@link RecyclingManager#getRawRecyclingOutputs(Level, ItemStack) recycled Item}.
 * This can add, remove or modify individual {@link RecyclingOutput}s based on the input Item Stack (and particularly its {@link ItemStack#getComponents() Components}).
 * <p>Recipe-dependent modifications should not be done with a {@link RecyclingOutputModifier} but by implementing {@link IRecyclableRecipe} on your Recipe.</p>
 * @see RecyclingManager#registerModifier(RecyclingOutputsModifier) Register a {@link RecyclingOutputsModifier}
 * @see RecyclingOutputsModifier#DURABILITY Example
 */
public interface RecyclingOutputsModifier extends Comparable<RecyclingOutputsModifier> {
    
    /**
     * Modify the {@link RecyclingOutputs} based on the Item Stack being recycled, independent of the Recipe used to craft it.
     * @param input Will always have count {@code 1}
     * @param outputs
     */
    public void modify(Level level, ItemStack input, RecyclingOutputs outputs);

    /**
     * {@link RecyclingOutputsModifier}s are applied in priority order.
     */
    public int getPriority();

    @Override
    public default int compareTo(RecyclingOutputsModifier o) {
        return getPriority() - o.getPriority();
    };

    /**
     * Scales the {@link RecyclingOutputs#getExpectationMultiplier() expected amounts} of all Item Stacks by the {@link DataComponents#DAMAGE} of the recycled Stack
     */
    public static RecyclingOutputsModifier DURABILITY = new RecyclingOutputsModifier() {

        @Override
        public void modify(Level level, ItemStack input, RecyclingOutputs outputs) {
            if (input.isDamaged()) outputs.multiplyAll((float)input.getDamageValue() / (float)input.getMaxDamage());
        };

        @Override
        public int getPriority() {
            return 0;
        };
        
    };

    /**
     * Propagates the {@link Contamination Contaminants} of the recycled Stack to the {@link RecyclingOutputs}
     */
    public static RecyclingOutputsModifier CONTAMINANTS = new RecyclingOutputsModifier() {

        @Override
        public void modify(Level level, ItemStack input, RecyclingOutputs outputs) {
            ItemContamination.get(input).streamAllContaminants().forEach(contaminant -> 
                outputs.splitAll(contaminant.value().preservationProportion, output -> 
                    ItemContamination.get(output.item).contaminate(contaminant)
                )
            );
        };

        @Override
        public int getPriority() {
            return 1000;
        };
        
    };

    /**
     * Minimizes the use of random chance by splitting fractions of {@link RecyclingOutputs} into smaller Items.
     * For example, 7.5 Iron Ingots (7 Ingots and a 50% chance of another) becomes 7 Ingots, 4 Nuggets and a 50% chance of another Nugget.
     */
    public static RecyclingOutputsModifier DECOMPRESSION = new RecyclingOutputsModifier() {
        
        @Override
        public void modify(Level level, ItemStack input, RecyclingOutputs outputs) {
            outputs.forEach(output -> {
                ItemCompressionManager.getSequence(output.item).ifPresent(sequence -> {
                    double remainder = output.getExpectedRemainder(); // Only decompress the remainder as passing the whole amount through the following procedure would also compress it
                    double baseItems = sequence.getEquivalentBaseItems(output.item, remainder);
                    long wholeBaseItems = (long)baseItems;
                    if (wholeBaseItems == 0) return;
                    List<BigItemStack> compressedOutputs = sequence.getFewestStacks(wholeBaseItems);
                    if (compressedOutputs.size() >= 2) { // Don't bother if it just spits back out the base Item
                        output.expectedCount -= remainder; // Take off the bit that gets decompressed
                        outputs.addAll(compressedOutputs.stream().map(RecyclingOutput::new).toList()); // Add on the decompressed bit
                        outputs.add(new RecyclingOutput(sequence.getBaseItem(), baseItems - wholeBaseItems)); // Add on whatever was too small to get decompressed
                    };
                });
            });
        };

        @Override
        public int getPriority() {
            return 2000;
        };
    };
};
