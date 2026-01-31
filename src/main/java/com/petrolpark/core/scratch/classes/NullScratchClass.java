package com.petrolpark.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public class NullScratchClass extends ExpressionDefaultParameterSimpleScratchClass<Unit> implements IByteBufScratchClass<Unit> {

    public static final StreamCodec<ByteBuf, Unit> STREAM_CODEC = StreamCodec.unit(Unit.INSTANCE);

    @Override
    public Unit fallback() {
        return Unit.INSTANCE;
    };

    @Override
    public Codec<Unit> codec() {
        return Unit.CODEC;
    };

    @Override
    public StreamCodec<ByteBuf, Unit> streamCodec() {
        return STREAM_CODEC;
    };

    @Override
    public <TO_TYPE> Optional<IScratchClass.Caster<Unit, TO_TYPE>> cast(IScratchClass<TO_TYPE> toClass) {
        return Optional.empty();
    };
    
};
