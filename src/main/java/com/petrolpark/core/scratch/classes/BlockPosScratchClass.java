package com.petrolpark.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public class BlockPosScratchClass extends ExpressionDefaultParameterSimpleScratchClass<BlockPos> implements IByteBufScratchClass<BlockPos> {

    @Override
    public BlockPos fallback() {
        return BlockPos.ZERO;
    };

    @Override
    public Codec<BlockPos> codec() {
        return BlockPos.CODEC;
    };

    @Override
    public StreamCodec<ByteBuf, BlockPos> streamCodec() {
        return BlockPos.STREAM_CODEC;
    };

    @Override
    public <TO_TYPE> Optional<IScratchClass.Caster<BlockPos, TO_TYPE>> cast(IScratchClass<TO_TYPE> toClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cast'");
    };
    
};
