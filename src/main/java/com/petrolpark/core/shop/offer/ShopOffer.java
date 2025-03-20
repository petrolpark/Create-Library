package com.petrolpark.core.shop.offer;

import java.util.List;
import java.util.Collections;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.data.reward.IReward;
import com.petrolpark.core.shop.offer.order.ShopOrder;

public record ShopOffer(List<IReward> rewards, ShopOrder order) {

    public static final ShopOffer EMPTY = new ShopOffer(Collections.emptyList(), ShopOrder.EMPTY);
  
    public static final Codec<ShopOffer> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.list(IReward.CODEC).fieldOf("rewards").forGetter(ShopOffer::rewards),
            ShopOrder.CODEC.fieldOf("order").forGetter(ShopOffer::order)
        ).apply(instance, ShopOffer::new)
    );
};
