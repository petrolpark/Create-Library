package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.reward.IAbstractReward;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;

@ParametersAreNonnullByDefault
public interface IWrappedEntityReward<REWARD extends IAbstractReward<?>> extends IEntityReward {

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
        IEntityReward.super.validate(context);
        reward().validate(context.forChild(".child_reward"));
    };
    
};
