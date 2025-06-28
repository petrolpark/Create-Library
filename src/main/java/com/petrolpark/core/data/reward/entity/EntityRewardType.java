package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.reward.INamedRewardType;

public record EntityRewardType(String translationKey, MapCodec<? extends IEntityReward> codec) implements INamedRewardType {
    
};
