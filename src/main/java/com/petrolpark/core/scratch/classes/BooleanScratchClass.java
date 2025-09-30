package com.petrolpark.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BooleanScratchClass implements IScratchClass<Boolean, ExpressionArgument<IScratchEnvironment, Boolean, ?>> {

    @Override
    public Codec<Boolean> codec() {
        return Codec.BOOL;
    };

    @Override
    public StreamCodec<ByteBuf, Boolean> streamCodec() {
        return ByteBufCodecs.BOOL;
    };

    @Override
    public ExpressionParameter<IScratchEnvironment, Boolean> createDefaultParameter(String key) {
        return ExpressionArgument.parameter(key, this);
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, Boolean, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        return Optional.empty();
    };
    
};
