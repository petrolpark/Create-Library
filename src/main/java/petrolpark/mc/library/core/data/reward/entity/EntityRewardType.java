package petrolpark.mc.library.core.data.reward.entity;

import com.mojang.serialization.MapCodec;

public record EntityRewardType(MapCodec<? extends IEntityReward> entityRewardCodec) implements IEntityReward.Type {
    
};
