package com.petrolpark.core.recipe.recycling;

import com.petrolpark.core.contamination.ItemContamination;

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
};
