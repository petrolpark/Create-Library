package com.petrolpark.core.item;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

public class SharedBucketItem extends BucketItem implements ISharedFeature {

    protected final SharedFeatureFlag featureFlag;

    public SharedBucketItem(SharedFeatureFlag featureFlag, Fluid content, Properties properties) {
        super(content, properties);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return featureFlag;
    };
    
};
