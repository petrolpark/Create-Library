package com.petrolpark.shared.world.item;

import com.petrolpark.core.world.item.MilkCurativeBucketItem;
import com.petrolpark.shared.ISharedFeature;
import com.petrolpark.shared.SharedFeatureFlag;

import net.minecraft.world.level.material.Fluid;

public class SharedMilkCurativeBucketItem extends MilkCurativeBucketItem implements ISharedFeature {

    protected final SharedFeatureFlag featureFlag;

    public SharedMilkCurativeBucketItem(SharedFeatureFlag featureFlag, Fluid content, Properties properties) {
        super(content, properties);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return featureFlag;
    };
    
};
