package com.petrolpark.itemdecay;

import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;

public abstract class SimpleDecayProductItem extends DecayingItem {

    public final Supplier<ItemStack> decayProduct;
    
    public SimpleDecayProductItem(Properties properties, Supplier<ItemStack> decayProduct) {
        super(properties);
        this.decayProduct = decayProduct;
    };

    @Override
    public ItemStack getDecayProduct(ItemStack stack) {
        return decayProduct.get();
    };
};
