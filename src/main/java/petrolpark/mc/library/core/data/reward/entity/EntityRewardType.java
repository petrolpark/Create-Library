package petrolpark.mc.library.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.data.reward.INamedRewardType;

public record EntityRewardType(String translationKey, MapCodec<? extends IEntityReward> codec) implements INamedRewardType {
    
};
