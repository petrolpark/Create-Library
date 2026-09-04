package petrolpark.mc.library.core.data.reward.info;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public interface IRewardInfo {

    /**
     * Use {@link IReward#DIRECT_CODEC} instead.
     */
    @ApiStatus.Internal
    public static final Codec<IRewardInfo> TYPED_CODEC = PetrolparkRegistries.REWARD_INFO_TYPES
        .byNameCodec()
        .dispatch("reward_info_type", IRewardInfo::getRewardInfoType, IRewardInfo.Type::infoCodec);

    public static final Codec<IRewardInfo> DIRECT_CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, IRewardInfo> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.REWARD_INFO_TYPE)
        .dispatch(IRewardInfo::getRewardInfoType, IRewardInfo.Type::infoStreamCodec);
    
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics);

    @OnlyIn(Dist.CLIENT)
    public void addToDescription(IndentedTooltipBuilder builder);

    public default Optional<ItemStack> getItemStack() {
        return Optional.empty();
    };

    public IRewardInfo.Type getRewardInfoType();

    public interface Type {

        public MapCodec<? extends IRewardInfo> infoCodec();

        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec();
    };
};
