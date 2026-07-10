package petrolpark.mc.library.core.world.restaurant.order;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public record ClientRestaurantOrder(
    int id,
    IAdvancedIngredient<? super ItemStack> ingredient,
    List<RestaurantOrderModifier.Info> modifiersInfo,
    List<IRewardInfo> rewardsInfo
) implements IRestaurantOrder {
    
    public static final StreamCodec<RegistryFriendlyByteBuf, IRestaurantOrder> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, IRestaurantOrder::id,
        ItemAdvancedIngredient.STREAM_CODEC, IRestaurantOrder::ingredient,
        RestaurantOrderModifier.Info.STREAM_CODEC.apply(ByteBufCodecs.list()), IRestaurantOrder::modifiersInfo,
        IRewardInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), IRestaurantOrder::rewardsInfo,
        ClientRestaurantOrder::new
    );
};
