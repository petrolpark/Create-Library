package com.petrolpark.core.shop.offer.order;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record ShopOrderModifierEntry(ShopOrderModifier orderModifier, NumberProvider chance, boolean hidden) {

    public static final Codec<ShopOrderModifierEntry> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            ShopOrderModifier.CODEC.fieldOf("modifier").forGetter(ShopOrderModifierEntry::orderModifier),
            NumberProviders.CODEC.optionalFieldOf("chance", ConstantValue.exactly(1f)).forGetter(ShopOrderModifierEntry::chance),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(ShopOrderModifierEntry::hidden)
        ).apply(instance, ShopOrderModifierEntry::new)
    ));
};
