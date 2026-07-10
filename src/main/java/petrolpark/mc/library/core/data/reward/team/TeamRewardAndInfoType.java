package petrolpark.mc.library.core.data.reward.team;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public record TeamRewardAndInfoType(
    String translationKey,
    MapCodec<? extends ITeamReward> teamRewardCodec,
    MapCodec<? extends IRewardInfo> infoCodec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec
) implements ITeamReward.Type, INamedRewardInfo.Type {
    
};
