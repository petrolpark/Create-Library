package com.petrolpark.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public class BlockPosScratchClass extends SimpleScratchClass<BlockPos> {

    @Override
    public Codec<BlockPos> codec() {
        return BlockPos.CODEC;
    };

    @Override
    public StreamCodec<ByteBuf, BlockPos> streamCodec() {
        return BlockPos.STREAM_CODEC;
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, BlockPos, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        return Optional.empty();
    };
    
};
