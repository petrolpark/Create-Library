package petrolpark.mc.library.shared.world.item;

import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

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
