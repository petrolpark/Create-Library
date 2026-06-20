package com.petrolpark.core.world.item.restaurant.offer;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.data.reward.IReward;
import com.petrolpark.core.world.item.restaurant.offer.order.RestaurantOrder;

public record RestaurantOffer(List<IReward> rewards, RestaurantOrder order) {

    public static final RestaurantOffer EMPTY = new RestaurantOffer(Collections.emptyList(), RestaurantOrder.EMPTY);
  
    public static final Codec<RestaurantOffer> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.list(IReward.CODEC).fieldOf("rewards").forGetter(RestaurantOffer::rewards),
            RestaurantOrder.CODEC.fieldOf("order").forGetter(RestaurantOffer::order)
        ).apply(instance, RestaurantOffer::new)
    );
};
