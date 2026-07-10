package petrolpark.mc.library.core.data.reward;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public record RewardAndInfoType(
    String translationKey,
    MapCodec<? extends IReward> rewardCodec,
    MapCodec<? extends IRewardInfo> infoCodec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec
) implements IReward.Type, INamedRewardInfo.Type {
    
};
