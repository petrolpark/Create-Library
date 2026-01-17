package com.petrolpark.core.scratch.classes;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.DropdownArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrDropdownArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.util.Lang;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BooleanScratchClass implements IScratchClass<Boolean> {

    public static final DropdownArgument.SimpleEntry<Boolean>
    TRUE = new DropdownArgument.SimpleEntry<>(true, Lang.generic("true")),
    FALSE = new DropdownArgument.SimpleEntry<>(false, Lang.generic("false"));

    public static final <ENVIRONMENT extends IScratchEnvironment> List<DropdownArgument.Entry<? super ENVIRONMENT, Boolean>> getValues() {
        return List.of(TRUE, FALSE);
    };

    @Override
    public Codec<Boolean> codec() {
        return Codec.BOOL;
    };

    @Override
    public StreamCodec<ByteBuf, Boolean> streamCodec() {
        return ByteBufCodecs.BOOL;
    };

    @Override
    public IScratchParameter<IScratchEnvironment, Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>> createDefaultParameter(String key) {
        return ExpressionOrDropdownArgument.booleanParameter(key);
    };

    @Override
    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, Boolean, TO_TYPE>> cast(IScratchClass<TO_TYPE> toClass) {
        return Optional.empty();
    };
    
};
