package com.petrolpark.core.shop.offer.order;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Collections;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ShopOrder {

    public static final ShopOrder EMPTY = new ShopOrder(Ingredient.EMPTY, Collections.emptyList());

    public static final Codec<ShopOrder> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ShopOrder::getRequiredItem),
            Codec.list(ShopOrderModifier.CODEC).fieldOf("orderModifiers").forGetter(ShopOrder::getOrderModifiers)
        ).apply(instance, ShopOrder::new)
    );
    
    protected final Ingredient requiredItem;
    protected final List<ShopOrderModifier> orderModifiers;

    public ShopOrder(Ingredient requiredItem, List<ShopOrderModifier> modifiers) {
        this.requiredItem = requiredItem;
        this.orderModifiers = modifiers;
    };

    public Ingredient getRequiredItem() {
        return requiredItem;
    };

    public List<ShopOrderModifier> getOrderModifiers() {
        return orderModifiers;
    };

    public boolean test(ItemStack stack) {
        return getRequiredItem().test(stack);
    };
};
