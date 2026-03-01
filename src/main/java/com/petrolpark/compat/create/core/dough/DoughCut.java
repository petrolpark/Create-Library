package com.petrolpark.compat.create.core.dough;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.create.PetrolparkCreateRegistries;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

@ApiStatus.Experimental
public record DoughCut(int pattern, float area) {

    public static final Codec<DoughCut> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.intRange(0, 255).fieldOf("pattern").forGetter(DoughCut::pattern),
        Codec.floatRange(0f, 1f).fieldOf("area").forGetter(DoughCut::area)
    ).apply(instance, DoughCut::new));

    public static final Codec<Holder<DoughCut>> CODEC = RegistryFileCodec.create(PetrolparkCreateRegistries.Keys.DOUGH_CUT, DIRECT_CODEC);
};
