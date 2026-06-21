package petrolpark.mc.library.shared.world.item;

import petrolpark.mc.library.core.world.item.MilkCurativeBucketItem;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

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
