package petrolpark.mc.library.core.data.reward;

import com.mojang.serialization.MapCodec;

public record RewardType(String translationKey, MapCodec<? extends IReward> codec) implements INamedRewardType {
    
};
