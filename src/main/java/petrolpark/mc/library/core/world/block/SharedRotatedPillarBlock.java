package petrolpark.mc.library.core.world.block;

import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SharedRotatedPillarBlock extends RotatedPillarBlock implements ISharedFeature {

    protected final SharedFeatureFlag featureFlag;

    public SharedRotatedPillarBlock(BlockBehaviour.Properties properties, SharedFeatureFlag featureFlag) {
        super(properties);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return featureFlag;
    };
    
};
