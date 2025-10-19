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

public class IntegerScratchClass implements IParseableScratchClass<Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>> {

    @Override
    public Codec<Long> codec() {
        return Codec.LONG;
    };

    @Override
    public StreamCodec<ByteBuf, Long> streamCodec() {
        return ByteBufCodecs.VAR_LONG;
    };

    @Override
    public ExpressionOrLiteralParameter<IScratchEnvironment, Long> createDefaultParameter(String key) {
        return ExpressionOrLiteralArgument.integerParameter(key);
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, Long, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cast'");
    };

    @Override
    public Long parse(String string) {
        // TODO Auto-generated method stub
        return 0l;
    };
    
};
