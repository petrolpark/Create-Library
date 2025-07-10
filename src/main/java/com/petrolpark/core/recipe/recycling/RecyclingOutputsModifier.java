package com.petrolpark.core.recipe.recycling;

import java.util.List;

import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.recipe.compression.ItemCompressionManager;
import com.petrolpark.util.BigItemStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface RecyclingOutputsModifier {
    
    /**
     * @param input Will have count {@code 1}
     * @param outputs
     */
    public void modify(Level level, ItemStack input, RecyclingOutputs outputs);

    public int getPriority();

    public static int compare(RecyclingOutputsModifier first, RecyclingOutputsModifier second) {
        return first.getPriority() - second.getPriority();
    };

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
     * Minimize the use of random chance by splitting fractions of outputs into smaller Items.
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
