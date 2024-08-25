package com.petrolpark.itemdecay;

import java.util.function.Supplier;

import com.simibubi.create.foundation.config.ConfigBase.ConfigInt;

import net.minecraft.world.item.ItemStack;

public class ConfiguredDecayingItem extends SimpleDecayProductItem {

    protected final Supplier<ConfigInt> lifetime;

    public ConfiguredDecayingItem(Properties properties, Supplier<ItemStack> decayProduct, Supplier<ConfigInt> lifetime) {
        super(properties, decayProduct);
        this.lifetime = lifetime;
    };

    @Override
    public long getLifetime(ItemStack stack) {
        return lifetime.get().get();
    };
    
};
