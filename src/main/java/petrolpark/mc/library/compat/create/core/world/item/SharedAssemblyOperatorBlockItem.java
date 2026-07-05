package petrolpark.mc.library.compat.create.core.world.item;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;

import net.minecraft.world.level.block.Block;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class SharedAssemblyOperatorBlockItem extends AssemblyOperatorBlockItem implements ISharedFeature {

    public final SharedFeatureFlag featureFlag;

    public SharedAssemblyOperatorBlockItem(Block block, Properties builder, SharedFeatureFlag featureFlag) {
        super(block, builder);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return featureFlag;
    };
    
};
