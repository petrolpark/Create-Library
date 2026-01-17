package com.petrolpark.core.scratch.classes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.DropdownArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.util.Lang;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;

public class DirectionScratchClass extends SimpleScratchClass<Direction> {

    public static final List<DropdownArgument.SimpleEntry<Direction>> VALUES = Stream.of(Direction.values()).map(direction -> new DropdownArgument.SimpleEntry<>(direction, Lang.direction(direction))).toList();

    @Override
    public Codec<Direction> codec() {
        return Direction.CODEC;
    };

    @Override
    public StreamCodec<ByteBuf, Direction> streamCodec() {
        return Direction.STREAM_CODEC;
    };

    @Override
    public IScratchParameter<IScratchEnvironment, Direction, ExpressionArgument<IScratchEnvironment, Direction, ?>> createDefaultParameter(String key) {
        // TODO Auto-generated method stub
        return null;
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, Direction, TO_TYPE>> cast(IScratchClass<TO_TYPE> toClass) {
        return Optional.empty();
    };
    
};
