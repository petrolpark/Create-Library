package com.petrolpark.item.decay;

import java.util.function.Supplier;

import com.petrolpark.RequiresCreate;
import net.createmod.catnip.config.ConfigBase.ConfigInt;

import net.minecraft.world.item.ItemStack;

@RequiresCreate
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
