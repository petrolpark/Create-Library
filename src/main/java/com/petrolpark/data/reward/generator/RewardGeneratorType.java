package com.petrolpark.data.reward.generator;

import com.mojang.serialization.MapCodec;

public record RewardGeneratorType(MapCodec<? extends IRewardGenerator> codec) {
    
};
