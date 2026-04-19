package com.petrolpark.core.contamination;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class ItemContaminationSavedEvent extends Event {
    
    public final ItemStack stack;
    public final ItemContamination contamination;

    public ItemContaminationSavedEvent(ItemStack stack, ItemContamination contamination) {
        this.stack = stack;
        this.contamination = contamination;
    };
};
