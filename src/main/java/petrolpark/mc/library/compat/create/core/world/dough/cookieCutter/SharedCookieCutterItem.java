package petrolpark.mc.library.compat.create.core.world.dough.cookieCutter;

import net.minecraft.world.item.Item;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class SharedCookieCutterItem extends CookieCutterItem implements ISharedFeature {

    private final SharedFeatureFlag featureFlag;

    public SharedCookieCutterItem(Item.Properties properties, SharedFeatureFlag featureFlag) {
        super(properties);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return featureFlag;
    };
    
};
