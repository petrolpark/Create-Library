package petrolpark.mc.library.core.world.restaurant.order;

import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public record ClientRestaurantOrder(
    int id,
    IAdvancedIngredient<ItemStack> ingredient,
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

    public record Description(List<Component> lines, IntList orderModifierLineIndicies, int rewardsLineIndex) {
        
        public static final ClientRestaurantOrder.Description EMPTY = new ClientRestaurantOrder.Description(Collections.emptyList(), IntLists.emptyList(), 0);
    };

};
