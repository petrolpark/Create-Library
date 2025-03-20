package com.petrolpark.core.data.reward;

import com.mojang.serialization.MapCodec;

public record RewardType(MapCodec<? extends IReward> codec) {
    
};
