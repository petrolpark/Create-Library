package petrolpark.mc.library.core.data.reward.entity;

import org.jetbrains.annotations.ApiStatus;

import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public interface ISimpleEntityReward extends IEntityReward, INamedRewardInfo {

    @Override
    @ApiStatus.NonExtendable
    @Deprecated
    public default IRewardInfo info() {
        return this;
    };

    @Override
    public EntityRewardAndInfoType getType();
  
    @Override
    @ApiStatus.NonExtendable
    @Deprecated
    public default EntityRewardAndInfoType getRewardInfoType() {
        return getType();
    };
};
