package com.petrolpark.compat.pquality;

import com.petrolpark.compat.Mods;

import net.minecraft.world.item.ItemStack;

public class OptionalQuality {
  
    public static final int multiply(ItemStack stack, int base) {
        if (Mods.PQUALITY.isLoaded()) {
            return base; //TODO
        } else {
            return base;
        }
    };
};
