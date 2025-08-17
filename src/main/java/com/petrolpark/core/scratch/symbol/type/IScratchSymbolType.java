package com.petrolpark.core.scratch.symbol.type;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchSymbolType<SYMBOL extends IScratchSymbol<?, ?>> {
    
    public Codec<SYMBOL> codec();

    public StreamCodec<? super RegistryFriendlyByteBuf, SYMBOL> streamCodec();
};
