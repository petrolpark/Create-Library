package petrolpark.mc.library.core.data.reward.entity;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public record EntityRewardAndInfoType(
    String translationKey,
    MapCodec<? extends IEntityReward> entityRewardCodec,
    MapCodec<? extends IRewardInfo> infoCodec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec
) implements IEntityReward.Type, INamedRewardInfo.Type {
    
};
