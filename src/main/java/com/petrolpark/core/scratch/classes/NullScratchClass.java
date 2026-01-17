package com.petrolpark.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public class NullScratchClass extends SimpleScratchClass<Unit> {

    public static final StreamCodec<ByteBuf, Unit> STREAM_CODEC = StreamCodec.unit(Unit.INSTANCE);

    @Override
    public Codec<Unit> codec() {
        return Unit.CODEC;
    };

    @Override
    public StreamCodec<ByteBuf, Unit> streamCodec() {
        return STREAM_CODEC;
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, Unit, TO_TYPE>> cast(IScratchClass<TO_TYPE> toClass) {
        return Optional.empty();
    };
    
};
