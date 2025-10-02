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

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public class DirectionScratchClass implements IEnumScratchClass<Direction, ExpressionArgument<IScratchEnvironment, Direction, ?>> {

    public static final List<DropdownArgument.Named<Direction>> VALUES = Stream.of(Direction.values()).<DropdownArgument.Named<Direction>>map(direction -> new DropdownArgument.Named<Direction>() {

        @Override
        public Direction value() {
            return direction;
        };

        @Override
        public Component name() {
            return Component.translatable("petrolpark.generic.direction."+direction.getSerializedName());
        };
    
    }).toList();

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
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, Direction, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        // TODO Auto-generated method stub
        return Optional.empty();
    };

    @Override
    public List<DropdownArgument.Named<Direction>> values() {
        return VALUES;
    };
    
};
