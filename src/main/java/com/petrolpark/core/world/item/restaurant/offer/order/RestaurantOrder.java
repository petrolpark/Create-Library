package com.petrolpark.core.world.item.restaurant.offer.order;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class RestaurantOrder {

    public static final RestaurantOrder EMPTY = new RestaurantOrder(Ingredient.EMPTY, Collections.emptyList());

    public static final Codec<RestaurantOrder> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(RestaurantOrder::getRequiredItem),
            Codec.list(RestaurantOrderModifier.CODEC).fieldOf("orderModifiers").forGetter(RestaurantOrder::getOrderModifiers)
        ).apply(instance, RestaurantOrder::new)
    );
    
    protected final Ingredient requiredItem;
    protected final List<RestaurantOrderModifier> orderModifiers;

    public RestaurantOrder(Ingredient requiredItem, List<RestaurantOrderModifier> modifiers) {
        this.requiredItem = requiredItem;
        this.orderModifiers = modifiers;
    };

    public Ingredient getRequiredItem() {
        return requiredItem;
    };

    public List<RestaurantOrderModifier> getOrderModifiers() {
        return orderModifiers;
    };

    public boolean test(ItemStack stack) {
        return getRequiredItem().test(stack);
    };
};
