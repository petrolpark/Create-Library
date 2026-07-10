package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;

@ParametersAreNonnullByDefault
public interface IWrappedReward<REWARD extends IAbstractReward<?>> extends IReward {

    public abstract REWARD reward();

    public abstract WrappedRewardInfo wrapInfo(IRewardInfo info);

    @Override
    @ApiStatus.NonExtendable
    @Deprecated
    public default WrappedRewardInfo info() {
        return wrapInfo(reward().info());
    };

    @Override
    public default void validate(ValidationContext context) {
        IReward.super.validate(context);
        reward().validate(context.forChild(".child_reward"));
    };
    
};
