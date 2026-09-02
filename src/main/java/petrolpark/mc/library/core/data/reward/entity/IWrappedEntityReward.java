package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.reward.IAbstractReward;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public interface IWrappedEntityReward<REWARD extends IAbstractReward<?>> extends IEntityReward {

    public abstract Holder<REWARD> rewardHolder();

    public abstract WrappedRewardInfo wrapInfo(IRewardInfo info);

    @Override
    @ApiStatus.NonExtendable
    @Deprecated
    public default WrappedRewardInfo info() {
        return wrapInfo(rewardHolder().value().info());
    };

    @Override
    public default void validate(ValidationContext context) {
        IEntityReward.super.validate(context);
        DataValidationHelper.validateHolder(rewardHolder(), context, ".childReward");
    };
    
};
