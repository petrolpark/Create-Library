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

public class RealScratchClass implements IParseableScratchClass<Double, ExpressionOrLiteralArgument<IScratchEnvironment, Double>> {

    @Override
    public Codec<Double> codec() {
        return Codec.DOUBLE;
    };

    @Override
    public StreamCodec<ByteBuf, Double> streamCodec() {
        return ByteBufCodecs.DOUBLE;
    };

    @Override
    public ExpressionOrLiteralParameter<IScratchEnvironment, Double> createDefaultParameter(String key) {
        return ExpressionOrLiteralArgument.realParameter(key);
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, Double, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cast'");
    };

    @Override
    public Double parse(String string) {
        // TODO Auto-generated method stub
        return 0d;
    };
    
};
