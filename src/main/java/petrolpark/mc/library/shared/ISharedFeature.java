package petrolpark.mc.library.shared;

import net.minecraft.world.item.Item;

/**
 * A feature (typically a registered object like an {@link Item}) enabled by a {@link SharedFeatureFlag}.
 * In JEI, the displayed "mod ID" will be a list of all Petrolpark Mods that {@link SharedFeatureFlag#enable(petrolpark.mc.library.compat.Mods) enable it}.
 */
public interface ISharedFeature {
    
    public SharedFeatureFlag getSharedFeatureFlag();

};
