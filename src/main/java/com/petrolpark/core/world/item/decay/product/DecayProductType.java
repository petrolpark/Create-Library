package com.petrolpark.core.world.item.decay.product;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DecayProductType(MapCodec<? extends IDecayProduct> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends IDecayProduct> streamCodec) {
    
};
