package com.petrolpark.core.recipe.compression;

import net.minecraft.world.item.ItemStack;

public record ItemCompression(int count, ItemStack result) implements IItemCompression {
    
};
