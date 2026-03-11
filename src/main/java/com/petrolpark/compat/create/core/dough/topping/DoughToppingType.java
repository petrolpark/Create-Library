package com.petrolpark.compat.create.core.dough.topping;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DoughToppingType<T extends IDoughTopping>(MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) implements IDoughTopping.Type<T> {
    
};
