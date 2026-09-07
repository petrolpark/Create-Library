package petrolpark.mc.library.core.data.reward.info;

import petrolpark.mc.library.registry.PetrolparkRewardTypes;

public final class ConditionalRewardInfo extends WrappedRewardInfo {

    public ConditionalRewardInfo(IRewardInfo wrapped) {
        super(wrapped);
    };

    @Override
    public RewardInfoType getRewardInfoType() {
        return PetrolparkRewardTypes.INFO_CONDITIONAL.get();
    };
    
};
