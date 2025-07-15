package com.petrolpark.core.item;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;

import net.minecraft.world.item.Item;

public class SharedItem extends Item implements ISharedFeature {

    protected final SharedFeatureFlag sharedFeatureFlag;

    public SharedItem(Properties properties, SharedFeatureFlag sharedFeatureFlag) {
        super(properties);
        this.sharedFeatureFlag = sharedFeatureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return sharedFeatureFlag;
    };
    
};
