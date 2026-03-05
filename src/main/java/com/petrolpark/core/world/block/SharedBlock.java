package com.petrolpark.core.world.block;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SharedBlock extends Block implements ISharedFeature {

    protected final SharedFeatureFlag featureFlag;

    public SharedBlock(BlockBehaviour.Properties properties, SharedFeatureFlag featureFlag) {
        super(properties);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return featureFlag;
    };
    
};
