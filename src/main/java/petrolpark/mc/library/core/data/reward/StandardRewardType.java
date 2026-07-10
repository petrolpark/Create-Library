package petrolpark.mc.library.core.data.reward;

import com.mojang.serialization.MapCodec;

public record StandardRewardType(MapCodec<? extends IReward> rewardCodec) implements IReward.Type {
    
};
