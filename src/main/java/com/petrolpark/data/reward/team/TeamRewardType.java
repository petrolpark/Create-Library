package com.petrolpark.data.reward.team;

import com.mojang.serialization.MapCodec;

public record TeamRewardType(MapCodec<? extends ITeamReward> codec) {
    
};
