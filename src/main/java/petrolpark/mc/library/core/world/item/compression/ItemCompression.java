package petrolpark.mc.library.core.world.item.compression;

import net.minecraft.world.item.ItemStack;

public record ItemCompression(int count, ItemStack result) implements IItemCompression {
    
};
