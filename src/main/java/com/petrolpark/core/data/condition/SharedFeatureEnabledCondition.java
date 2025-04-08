package com.petrolpark.core.data.condition;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.SharedFeatures;
import com.petrolpark.util.CodecHelper;

import net.neoforged.neoforge.common.conditions.ICondition;

public record SharedFeatureEnabledCondition(SharedFeatures feature) implements ICondition {

    public static final MapCodec<SharedFeatureEnabledCondition> CODEC = CodecHelper.singleFieldMap(SharedFeatures.CODEC, "feature", SharedFeatureEnabledCondition::feature, SharedFeatureEnabledCondition::new);

    @Override
    public boolean test(@Nonnull IContext context) {
        return feature.enabled();
    };

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    };
    
};
