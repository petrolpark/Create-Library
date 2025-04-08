package com.petrolpark.compat;

import com.tterrag.registrate.util.nullness.NonNullBiFunction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class SharedFeatureBlockItem extends BlockItem implements ISharedFeature {

    public static final NonNullBiFunction<Block, Properties, SharedFeatureBlockItem> of(SharedFeatures feature) {
        return (block, properties) -> new SharedFeatureBlockItem(block, properties, feature);
    };

    public final SharedFeatures feature;

    public SharedFeatureBlockItem(Block block, Properties properties, SharedFeatures feature) {
        super(block, properties);
        this.feature = feature;
    };

    @Override
    public SharedFeatures getSharedFeature() {
        return feature;
    };
    
};
