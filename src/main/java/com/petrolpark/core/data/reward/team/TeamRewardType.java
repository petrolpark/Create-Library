package com.petrolpark.core.data.reward.team;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.reward.INamedRewardType;

public record TeamRewardType(String translationKey, MapCodec<? extends ITeamReward> codec) implements INamedRewardType {
    
};
