package petrolpark.mc.library.compat.pquality;

import petrolpark.mc.library.compat.Mods;

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
