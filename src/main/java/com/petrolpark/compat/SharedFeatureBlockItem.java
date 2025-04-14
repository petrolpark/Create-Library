package com.petrolpark.compat;

import com.tterrag.registrate.util.nullness.NonNullBiFunction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class SharedFeatureBlockItem extends BlockItem implements ISharedFeature {

    public static final NonNullBiFunction<Block, Properties, SharedFeatureBlockItem> of(SharedFeatureFlag featureFlag) {
        return (block, properties) -> new SharedFeatureBlockItem(block, properties, featureFlag);
    };

    public final SharedFeatureFlag feature;

    public SharedFeatureBlockItem(Block block, Properties properties, SharedFeatureFlag feature) {
        super(block, properties);
        this.feature = feature;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return feature;
    };
    
};
