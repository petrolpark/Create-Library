package petrolpark.mc.library.core.data.reward;

import org.jetbrains.annotations.ApiStatus;

import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public interface ISimpleReward extends IReward, INamedRewardInfo {

    @Override
    @ApiStatus.NonExtendable
    @Deprecated
    public default IRewardInfo info() {
        return this;
    };

    @Override
    public RewardAndInfoType getType();
    
    @Override
    @ApiStatus.NonExtendable
    @Deprecated
    public default RewardAndInfoType getRewardInfoType() {
        return getType();
    };
};
