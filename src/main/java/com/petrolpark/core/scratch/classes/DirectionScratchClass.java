package com.petrolpark.core.scratch.classes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.argument.DropdownArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument.ExpressionOrDropdownParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.util.Lang;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;

public class DirectionScratchClass extends SimpleScratchClass<Direction> implements IByteBufScratchClass<Direction> {

    public static final List<DropdownArgument.Entry<? super IScratchEnvironment, Direction>> VALUES = Stream.of(Direction.values()).<DropdownArgument.Entry<? super IScratchEnvironment, Direction>>map(direction -> new DropdownArgument.SimpleEntry<>(direction, Lang.direction(direction))).toList();

    @Override
    public Direction fallback() {
        return Direction.NORTH;
    };

    @Override
    public Codec<Direction> codec() {
        return Direction.CODEC;
    };

    @Override
    public StreamCodec<ByteBuf, Direction> streamCodec() {
        return Direction.STREAM_CODEC;
    };

    @Override
    public ExpressionOrDropdownParameter<IScratchEnvironment, Direction> createDefaultParameter(String key) {
        return new ExpressionOrDropdownParameter<>(key, this, VALUES);
    };

    @Override
    public <TO_TYPE> Optional<IScratchClass.Caster<Direction, TO_TYPE>> cast(IScratchClass<TO_TYPE> toClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cast'");
    };
    
};
