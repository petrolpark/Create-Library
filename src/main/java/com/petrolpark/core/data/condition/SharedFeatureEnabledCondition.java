package com.petrolpark.core.data.condition;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkDataLoadingConditions;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.util.CodecHelper;

import net.neoforged.neoforge.common.conditions.ICondition;

public record SharedFeatureEnabledCondition(SharedFeatureFlag featureFlag) implements ICondition {

    public static final MapCodec<SharedFeatureEnabledCondition> CODEC = CodecHelper.singleFieldMap(SharedFeatureFlag.CODEC, "feature", SharedFeatureEnabledCondition::featureFlag, SharedFeatureEnabledCondition::new);

    @Override
    public boolean test(@Nonnull IContext context) {
        return featureFlag.enabled();
    };

    @Override
    public MapCodec<? extends ICondition> codec() {
        return PetrolparkDataLoadingConditions.FEATURE_ENABLED.get();
    };
    
};
