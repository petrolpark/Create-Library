package petrolpark.mc.library.core.data.reward.team;

import com.mojang.serialization.MapCodec;

public record StandardTeamRewardType(MapCodec<? extends ITeamReward> teamRewardCodec) implements ITeamReward.Type {
    
};
