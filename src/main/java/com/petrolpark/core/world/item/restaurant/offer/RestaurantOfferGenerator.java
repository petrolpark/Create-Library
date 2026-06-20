package com.petrolpark.core.world.item.restaurant.offer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.data.recipe.ingredient.randomizer.IngredientRandomizer;
import com.petrolpark.core.data.reward.generator.IRewardGenerator;
import com.petrolpark.core.world.item.restaurant.Restaurant;
import com.petrolpark.core.world.item.restaurant.offer.order.RestaurantOrder;
import com.petrolpark.core.world.item.restaurant.offer.order.RestaurantOrderModifierEntry;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class RestaurantOfferGenerator implements LootContextUser {

    public static final Codec<RestaurantOfferGenerator> DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            NumberProviders.CODEC.optionalFieldOf("time", ConstantValue.exactly(-1)).forGetter(RestaurantOfferGenerator::getTimeGenerator),
            IRewardGenerator.CODEC.fieldOf("reward").forGetter(RestaurantOfferGenerator::getRewardGenerator),
            IngredientRandomizer.CODEC.fieldOf("order").forGetter(RestaurantOfferGenerator::getOrderRandomizer),
            Codec.list(RestaurantOrderModifierEntry.CODEC).optionalFieldOf("orderModifiers", Collections.emptyList()).forGetter(RestaurantOfferGenerator::getOrderModifiers)
        ).apply(instance, RestaurantOfferGenerator::new)
    ));

    public final NumberProvider timeGenerator;
    public final IRewardGenerator rewardGenerator;
    public final IngredientRandomizer orderRandomizer;
    public final List<RestaurantOrderModifierEntry> orderModifiers;
    
    public RestaurantOfferGenerator(NumberProvider timeGenerator, IRewardGenerator rewardGenerator, IngredientRandomizer orderRandomizer, List<RestaurantOrderModifierEntry> orderModifiers) {
        this.timeGenerator = timeGenerator;
        this.rewardGenerator = rewardGenerator;
        this.orderRandomizer = orderRandomizer;
        this.orderModifiers = orderModifiers;
    };

    public RestaurantOffer generate(LootContext context, Restaurant restaurant) {
        return new RestaurantOffer(rewardGenerator.generate(context), new RestaurantOrder(orderRandomizer.generate(context), Stream.concat(orderModifiers.stream(), restaurant.getGlobalOrderModifierEntries().stream()).filter(ome -> ome.chance().getFloat(context) < context.getRandom().nextFloat()).map(RestaurantOrderModifierEntry::orderModifier).toList()));
    };

    public NumberProvider getTimeGenerator() {
        return timeGenerator;
    };

    public IRewardGenerator getRewardGenerator() {
        return rewardGenerator;
    };

    public IngredientRandomizer getOrderRandomizer() {
        return orderRandomizer;
    };

    public List<RestaurantOrderModifierEntry> getOrderModifiers() {
        return orderModifiers;
    };
};
