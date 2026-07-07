package petrolpark.mc.library.core.data.reward;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RewardType(String translationKey, MapCodec<? extends IReward> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IReward> streamCodec) implements INamedRewardType {
    
};
