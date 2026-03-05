package com.petrolpark.compat.create.core.dough;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.wooden.WoodenItem;

import net.minecraft.world.item.Item;

public class RollingPinItem extends WoodenItem implements ISharedFeature {

    public RollingPinItem(Item.Properties properties) {
        super(properties);
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.ROLLING_PIN;
    };
    
};
