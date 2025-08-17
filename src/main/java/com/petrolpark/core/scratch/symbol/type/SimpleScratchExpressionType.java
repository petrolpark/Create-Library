package com.petrolpark.core.scratch.symbol.type;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SimpleScratchExpressionType<EXPRESSION extends IScratchExpression<?, ?, ?>>(
    Codec<EXPRESSION> codec,
    StreamCodec<? super RegistryFriendlyByteBuf, EXPRESSION> streamCodec
) implements IScratchExpressionType<EXPRESSION> {
    
};
