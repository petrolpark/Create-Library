package com.petrolpark.compat.create.common.processing.mandrel.animation;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MandrelAnimationType(MapCodec<? extends IMandrelAnimation> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IMandrelAnimation> streamCodec) {
    
};
