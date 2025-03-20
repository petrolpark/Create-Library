package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;

public record EntityRewardType(MapCodec<? extends IEntityReward> codec) {
    
};
