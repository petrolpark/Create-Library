package petrolpark.mc.library.core.data.reward.info;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RewardInfoType(String translationKey, MapCodec<? extends IRewardInfo> infoCodec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec) implements IRewardInfo.Type {
    
};
