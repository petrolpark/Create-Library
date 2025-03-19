package com.petrolpark.shop.offer;

import java.util.List;
import java.util.stream.Stream;
import java.util.Collections;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.reward.generator.IRewardGenerator;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizer;
import com.petrolpark.shop.Shop;
import com.petrolpark.shop.offer.order.ShopOrder;
import com.petrolpark.shop.offer.order.ShopOrderModifierEntry;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class ShopOfferGenerator implements LootContextUser {

    public static final Codec<ShopOfferGenerator> DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            NumberProviders.CODEC.optionalFieldOf("time", ConstantValue.exactly(-1)).forGetter(ShopOfferGenerator::getTimeGenerator),
            IRewardGenerator.CODEC.fieldOf("reward").forGetter(ShopOfferGenerator::getRewardGenerator),
            IngredientRandomizer.CODEC.fieldOf("order").forGetter(ShopOfferGenerator::getOrderRandomizer),
            Codec.list(ShopOrderModifierEntry.CODEC).optionalFieldOf("orderModifiers", Collections.emptyList()).forGetter(ShopOfferGenerator::getOrderModifiers)
        ).apply(instance, ShopOfferGenerator::new)
    ));

    public final NumberProvider timeGenerator;
    public final IRewardGenerator rewardGenerator;
    public final IngredientRandomizer orderRandomizer;
    public final List<ShopOrderModifierEntry> orderModifiers;
    
    public ShopOfferGenerator(NumberProvider timeGenerator, IRewardGenerator rewardGenerator, IngredientRandomizer orderRandomizer, List<ShopOrderModifierEntry> orderModifiers) {
        this.timeGenerator = timeGenerator;
        this.rewardGenerator = rewardGenerator;
        this.orderRandomizer = orderRandomizer;
        this.orderModifiers = orderModifiers;
    };

    public ShopOffer generate(LootContext context, Shop shop) {
        return new ShopOffer(rewardGenerator.generate(context), new ShopOrder(orderRandomizer.generate(context), Stream.concat(orderModifiers.stream(), shop.getGlobalOrderModifierEntries().stream()).filter(ome -> ome.chance().getFloat(context) < context.getRandom().nextFloat()).map(ShopOrderModifierEntry::orderModifier).toList()));
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

    public List<ShopOrderModifierEntry> getOrderModifiers() {
        return orderModifiers;
    };
};
