package com.petrolpark.core.data.loot.numberprovider;

import net.minecraft.world.level.storage.loot.LootContext;

public interface IEstimableNumberProvider {
    
    /**
     * A rough estimate for the output of this Number Provider, for display purposes only.
     */
    public NumberEstimate getEstimate();

    /**
     * The maximum possible value this Number Provider can give, ignoring any randomness.
     */
    public float getMaxFloat(LootContext context);
};
