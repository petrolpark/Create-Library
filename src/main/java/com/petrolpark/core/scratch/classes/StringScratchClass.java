package com.petrolpark.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StringScratchClass implements IScratchClass<String> {

    @Override
    public Codec<String> codec() {
        return Codec.STRING;
    };

    @Override
    public StreamCodec<ByteBuf, String> streamCodec() {
        return ByteBufCodecs.STRING_UTF8;
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment> ExpressionOrLiteralParameter<ENVIRONMENT, String> createDefaultParameter(String key) {
        return ExpressionOrLiteralArgument.stringParameter(key);
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, String, TO_TYPE>> cast(IScratchClass<TO_TYPE> toClass) {
        return Optional.empty();
    };
    
};
