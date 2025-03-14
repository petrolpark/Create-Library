package com.petrolpark.data.loot.numberprovider;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record SigmoidNumberProvider(NumberProvider shallowness, NumberProvider midpoint, NumberProvider value) implements NumberProvider {

    public static final MapCodec<SigmoidNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NumberProviders.CODEC.fieldOf("shallowness").forGetter(SigmoidNumberProvider::shallowness),
        NumberProviders.CODEC.fieldOf("midpoint").forGetter(SigmoidNumberProvider::midpoint),
        NumberProviders.CODEC.fieldOf("value").forGetter(SigmoidNumberProvider::value)
    ).apply(instance, SigmoidNumberProvider::new));

    @Override
    public float getFloat(@Nonnull LootContext lootContext) {
        float shallowness = this.shallowness.getFloat(lootContext);
        if (shallowness == 0f) return 1f;
        return 1f / (1f + (float)Math.exp((midpoint.getFloat(lootContext) - value.getFloat(lootContext)) / shallowness));
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.SIGMOID.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(shallowness.getReferencedContextParams(), midpoint.getReferencedContextParams());
    };
    
};
