package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public interface IWrappedReward<REWARD extends IAbstractReward<?>> extends IReward {

    public abstract Holder<REWARD> rewardHolder();

    public abstract IRewardInfo wrapInfo(IRewardInfo info);

    @Override
    @ApiStatus.NonExtendable
    @Deprecated
    public default IRewardInfo info() {
        return wrapInfo(rewardHolder().value().info());
    };

    @Override
    public default void validate(ValidationContext context) {
        IReward.super.validate(context);
        DataValidationHelper.validateHolder(rewardHolder(), context, "child_reward");
    };
    
};
