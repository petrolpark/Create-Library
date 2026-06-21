package petrolpark.mc.library.core.data.reward.team;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.data.reward.INamedRewardType;

public record TeamRewardType(String translationKey, MapCodec<? extends ITeamReward> codec) implements INamedRewardType {
    
};
