package com.petrolpark.data.loot.numberprovider;

import java.util.List;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public abstract class FunctionNumberProvider implements NumberProvider {

    public static final <PROVIDER extends FunctionNumberProvider> MapCodec<PROVIDER> codec(Function<List<NumberProvider>, PROVIDER> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.listOf().fieldOf("values").forGetter(FunctionNumberProvider::getChildren)
        ).apply(instance, constructor));
    };

    protected final List<NumberProvider> children;

    public FunctionNumberProvider(List<NumberProvider> children) {
        this.children = children;
    };

    public List<NumberProvider> getChildren() {
        return children;
    };

    @Override
    public final float getFloat(@Nonnull LootContext lootContext) {
        return apply(lootContext, children.stream().mapToDouble(child -> child.getFloat(lootContext)));
    };

    public abstract float apply(LootContext lootContext, DoubleStream childResults);
    
};
