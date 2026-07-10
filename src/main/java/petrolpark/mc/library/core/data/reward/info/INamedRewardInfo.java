package petrolpark.mc.library.core.data.reward.info;

import net.minecraft.network.chat.Component;

public interface INamedRewardInfo extends IRewardInfo {
    
    @Override
    public INamedRewardInfo.Type getRewardInfoType();

    default Component translate(String postfix, Object... args) {
        return Component.translatable(getRewardInfoType().translationKey() + "." + postfix, args);
    };

    default Component translateSimple(Object... args) {
        return Component.translatable(getRewardInfoType().translationKey(), args);
    };

    public interface Type extends IRewardInfo.Type {

        public String translationKey();
    };
};
