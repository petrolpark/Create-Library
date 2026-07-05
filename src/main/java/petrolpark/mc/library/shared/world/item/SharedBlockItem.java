package petrolpark.mc.library.shared.world.item;

import com.tterrag.registrate.util.nullness.NonNullBiFunction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class SharedBlockItem extends BlockItem implements ISharedFeature {

    public static final NonNullBiFunction<Block, Properties, SharedBlockItem> of(SharedFeatureFlag featureFlag) {
        return (block, properties) -> new SharedBlockItem(block, properties, featureFlag);
    };

    public final SharedFeatureFlag feature;

    public SharedBlockItem(Block block, Properties properties, SharedFeatureFlag feature) {
        super(block, properties);
        this.feature = feature;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return feature;
    };
    
};
